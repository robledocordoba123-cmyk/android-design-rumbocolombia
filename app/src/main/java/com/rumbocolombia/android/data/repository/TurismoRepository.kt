package com.rumbocolombia.android.data.repository

import com.rumbocolombia.android.data.api.ApiClient
import com.rumbocolombia.android.data.api.ApiColombiaService
import com.rumbocolombia.android.data.dto.AeropuertoCercano
import com.rumbocolombia.android.data.dto.Airport
import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.dto.City
import com.rumbocolombia.android.data.dto.Department
import com.rumbocolombia.android.data.dto.DetalleAtraccion
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.dto.ResultadosBusqueda
import com.rumbocolombia.android.data.dto.TouristicAttraction
import com.rumbocolombia.android.data.local.AtraccionDao
import com.rumbocolombia.android.data.local.BaseDeDatos
import com.rumbocolombia.android.data.local.GuardadaEntity
import com.rumbocolombia.android.data.local.aEntity
import com.rumbocolombia.android.data.local.aModelo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Interfaz que define las operaciones que el Repositorio de Turismo ofrece a la aplicación.
 * Los ViewModels le piden los datos a ella y no saben de dónde salen.
 */
interface TurismoRepository {
    suspend fun obtenerRegiones(): Result<List<Region>>
    suspend fun obtenerAtracciones(): Result<List<AtraccionConUbicacion>>
    suspend fun obtenerDetalle(id: Int): Result<DetalleAtraccion>
    suspend fun buscar(texto: String): Result<ResultadosBusqueda>
    suspend fun departamentoDe(ciudad: City): Result<Department?>

    // Atracciones guardadas por el usuario (tabla guardadas de Room)
    val guardadas: Flow<Set<Int>>
    suspend fun guardarAtraccion(id: Int)
    suspend fun eliminarGuardada(id: Int)
}

/**
 * Implementación concreta del Repositorio. Usa 2 fuentes de datos:
 * - Remota: la API de red (ApiColombiaService con Retrofit).
 * - Local: la base de datos Room del celular (AtraccionDao).
 *
 * Primero pide a la API y guarda la respuesta en Room. Si no hay internet,
 * responde con lo que quedó guardado en el celular. Los ViewModels no saben
 * de cuál de las dos fuentes salieron los datos.
 *
 * Cada función usa runCatching: si sale bien devuelve Result.success, y si
 * falla (sin internet y sin datos guardados, error 404/500, JSON inválido)
 * devuelve Result.failure sin que la app se cierre.
 */
class TurismoRepositoryImpl(
    private val api: ApiColombiaService = ApiClient.apiColombiaService,
    private val dao: AtraccionDao = BaseDeDatos.obtener().atraccionDao()
) : TurismoRepository {

    // Cada pantalla crea su propio TurismoRepositoryImpl, así que la memoria
    // de datos que deben compartir las 3 pantallas va en el companion object.
    private companion object {
        // Regiones, departamentos y aeropuertos casi no cambian: se piden una
        // vez y se guardan en memoria para no repetir la petición.
        var regiones: List<Region>? = null
        var departamentos: List<Department>? = null
        var aeropuertos: List<Airport>? = null

        // Última lista de atracciones recibida. La búsqueda de la API no trae la
        // ciudad de cada atracción, así que se toma de aquí.
        var atracciones: List<TouristicAttraction> = emptyList()
    }

    // Room avisa con un Flow cada vez que cambia la tabla guardadas
    override val guardadas: Flow<Set<Int>> = dao.observarGuardadas().map { it.toSet() }

    // Todas las peticiones van en Dispatchers.IO, el hilo para red y base de datos.
    override suspend fun obtenerRegiones(): Result<List<Region>> {
        return runCatching {
            withContext(Dispatchers.IO) {
                regiones ?: desdeApiOLocal(
                    api = { api.obtenerRegiones().also { regiones = it } },
                    // Sin internet: las regiones de las atracciones guardadas en el celular
                    local = {
                        dao.obtenerAtracciones().mapNotNull { it.aModelo().region }.distinctBy { it.id }
                    }
                )
            }
        }
    }

    override suspend fun obtenerAtracciones(): Result<List<AtraccionConUbicacion>> {
        return runCatching {
            withContext(Dispatchers.IO) {
                desdeApiOLocal(
                    api = {
                        val lista = api.obtenerAtracciones().also { atracciones = it }
                        lista.map { conUbicacion(it) }
                            .also { dao.guardarAtracciones(it.map { item -> item.aEntity() }) }
                    },
                    local = { dao.obtenerAtracciones().map { it.aModelo() } }
                )
            }
        }
    }

    override suspend fun obtenerDetalle(id: Int): Result<DetalleAtraccion> {
        return runCatching {
            withContext(Dispatchers.IO) {
                desdeApiOLocal(
                    api = {
                        val atraccion = api.obtenerAtraccion(id)
                        val item = conUbicacion(atraccion)
                        dao.guardarAtracciones(listOf(item.aEntity()))
                        DetalleAtraccion(item = item, aeropuertoCercano = aeropuertoMasCercano(atraccion))
                    },
                    // Sin internet: la atracción guardada, sin aeropuerto (ese dato viene de la API)
                    local = {
                        dao.obtenerAtraccion(id)?.let { DetalleAtraccion(item = it.aModelo(), aeropuertoCercano = null) }
                    }
                )
            }
        }
    }

    override suspend fun buscar(texto: String): Result<ResultadosBusqueda> {
        return runCatching {
            withContext(Dispatchers.IO) {
                desdeApiOLocal(
                    api = {
                        ResultadosBusqueda(
                            ciudades = api.buscarCiudades(texto),
                            atracciones = api.buscarAtracciones(texto).map { conUbicacion(it) }
                        )
                    },
                    // Sin internet: busca por nombre o ciudad en las atracciones guardadas
                    local = {
                        val encontradas = dao.buscar(texto).map { it.aModelo() }
                        if (encontradas.isEmpty()) null else ResultadosBusqueda(emptyList(), encontradas)
                    }
                )
            }
        }
    }

    override suspend fun departamentoDe(ciudad: City): Result<Department?> {
        return runCatching {
            withContext(Dispatchers.IO) {
                obtenerDepartamentos().find { it.id == ciudad.departmentId }
            }
        }
    }

    override suspend fun guardarAtraccion(id: Int) {
        withContext(Dispatchers.IO) { dao.guardar(GuardadaEntity(id)) }
    }

    override suspend fun eliminarGuardada(id: Int) {
        withContext(Dispatchers.IO) { dao.eliminarGuardada(id) }
    }

    // Intenta primero con la API. Si falla (por ejemplo, sin internet), usa los datos
    // guardados en Room. Si tampoco hay nada guardado, lanza el error original para
    // que la pantalla muestre "Sin conexión" y el botón Reintentar.
    private suspend fun <T> desdeApiOLocal(api: suspend () -> T, local: suspend () -> T?): T {
        return try {
            api()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val guardado = local()
            if (guardado == null || (guardado is List<*> && guardado.isEmpty())) throw e
            guardado
        }
    }

    private suspend fun obtenerDepartamentos(): List<Department> =
        departamentos ?: api.obtenerDepartamentos().also { departamentos = it }

    // En el JSON de /Airport la API trae latitude y longitude al revés
    // (ej. El Dorado: latitude -74.16, longitude 4.69). Aquí se corrigen.
    private suspend fun obtenerAeropuertos(): List<Airport> =
        aeropuertos ?: api.obtenerAeropuertos()
            .map { it.copy(latitude = it.longitude, longitude = it.latitude) }
            .also { aeropuertos = it }

    // Une la atracción con su ciudad, departamento y región.
    private suspend fun conUbicacion(atraccion: TouristicAttraction): AtraccionConUbicacion {
        val ciudad = atraccion.city ?: ciudadDe(atraccion)
        val departamento = obtenerDepartamentos().find { it.id == ciudad?.departmentId }
        val region = obtenerRegiones().getOrThrow().find { it.id == departamento?.regionId }
        return AtraccionConUbicacion(atraccion, ciudad, departamento, region)
    }

    private suspend fun ciudadDe(atraccion: TouristicAttraction): City? {
        if (atracciones.isEmpty()) atracciones = api.obtenerAtracciones()
        return atracciones.find { it.id == atraccion.id }?.city
    }

    private suspend fun aeropuertoMasCercano(atraccion: TouristicAttraction): AeropuertoCercano? =
        obtenerAeropuertos()
            .map { AeropuertoCercano(it, distanciaKm(atraccion.latitude, atraccion.longitude, it.latitude, it.longitude)) }
            .minByOrNull { it.distanciaKm }

    // Distancia en línea recta entre dos coordenadas (fórmula de Haversine).
    private fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val radioTierra = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return (radioTierra * 2 * atan2(sqrt(a), sqrt(1 - a))).toInt()
    }
}
