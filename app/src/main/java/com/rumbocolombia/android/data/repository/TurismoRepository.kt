package com.rumbocolombia.android.data.repository

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
import com.rumbocolombia.android.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// El Repositorio es el "jefe" de la capa de datos: los ViewModels le piden
// los datos a él y no saben de dónde salen. Ahora los pide a API Colombia
// con Retrofit. Es un object para que las 3 pantallas compartan los mismos
// datos (en la sesión de Hilt se inyectará en lugar de usar object).
object TurismoRepository {

    private val api: ApiColombiaService = RetrofitClient.api

    // Regiones, departamentos y aeropuertos casi no cambian: se piden una
    // vez y se guardan en memoria para no repetir la petición.
    private var regiones: List<Region>? = null
    private var departamentos: List<Department>? = null
    private var aeropuertos: List<Airport>? = null

    // Última lista de atracciones recibida. La búsqueda de la API no trae la
    // ciudad de cada atracción, así que se toma de aquí.
    private var atracciones: List<TouristicAttraction> = emptyList()

    // Atracciones guardadas para ver sin internet (por ahora en memoria;
    // en la sesión de Room se guardarán en el celular).
    private val _guardadas = MutableStateFlow<Set<Int>>(emptySet())
    val guardadas: StateFlow<Set<Int>> = _guardadas.asStateFlow()

    // Todas las peticiones van en Dispatchers.IO, el hilo para red.
    suspend fun obtenerRegiones(): List<Region> = withContext(Dispatchers.IO) {
        regiones ?: api.obtenerRegiones().also { regiones = it }
    }

    suspend fun obtenerAtracciones(): List<AtraccionConUbicacion> = withContext(Dispatchers.IO) {
        val lista = api.obtenerAtracciones().also { atracciones = it }
        lista.map { conUbicacion(it) }
    }

    suspend fun obtenerDetalle(id: Int): DetalleAtraccion = withContext(Dispatchers.IO) {
        val atraccion = api.obtenerAtraccion(id)
        DetalleAtraccion(
            item = conUbicacion(atraccion),
            aeropuertoCercano = aeropuertoMasCercano(atraccion)
        )
    }

    suspend fun buscar(texto: String): ResultadosBusqueda = withContext(Dispatchers.IO) {
        ResultadosBusqueda(
            ciudades = api.buscarCiudades(texto),
            atracciones = api.buscarAtracciones(texto).map { conUbicacion(it) }
        )
    }

    suspend fun departamentoDe(ciudad: City): Department? =
        obtenerDepartamentos().find { it.id == ciudad.departmentId }

    fun guardarAtraccion(id: Int) {
        _guardadas.value = _guardadas.value + id
    }

    fun eliminarGuardada(id: Int) {
        _guardadas.value = _guardadas.value - id
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
        val region = obtenerRegiones().find { it.id == departamento?.regionId }
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
