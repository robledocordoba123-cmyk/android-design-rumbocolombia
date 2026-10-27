package com.rumbocolombia.android.data.repository

import com.rumbocolombia.android.data.dto.AeropuertoCercano
import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.dto.City
import com.rumbocolombia.android.data.dto.Department
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.dto.TouristicAttraction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// El Repositorio es el "jefe" de la capa de datos: los ViewModels le piden
// los datos a él y no saben de dónde salen. Por ahora los saca de
// DatosDeEjemplo; en la sesión de Retrofit solo cambia este archivo.
// Es un object para que las 3 pantallas compartan los mismos datos
// (en la sesión de Hilt se inyectará en lugar de usar object).
object TurismoRepository {

    private val datos = DatosDeEjemplo

    // Atracciones guardadas para ver sin internet (por ahora en memoria;
    // en la sesión de Room se guardarán en el celular).
    private val _guardadas = MutableStateFlow<Set<Int>>(emptySet())
    val guardadas: StateFlow<Set<Int>> = _guardadas.asStateFlow()

    fun obtenerRegiones(): List<Region> = datos.regiones

    fun obtenerAtracciones(regionId: Int? = null): List<AtraccionConUbicacion> =
        datos.atracciones
            .map { conUbicacion(it) }
            .filter { regionId == null || it.region?.id == regionId }

    fun obtenerAtraccion(id: Int): AtraccionConUbicacion? =
        datos.atracciones.find { it.id == id }?.let { conUbicacion(it) }

    fun buscarCiudades(texto: String): List<City> =
        datos.ciudades.filter { it.name.contains(texto, ignoreCase = true) }

    fun buscarAtracciones(texto: String): List<AtraccionConUbicacion> =
        obtenerAtracciones().filter {
            it.atraccion.name.contains(texto, ignoreCase = true) ||
                it.ciudad?.name?.contains(texto, ignoreCase = true) == true
        }

    fun departamentoDe(ciudad: City): Department? =
        datos.departamentos.find { it.id == ciudad.departmentId }

    fun aeropuertoMasCercano(atraccion: TouristicAttraction): AeropuertoCercano? =
        datos.aeropuertos
            .map { AeropuertoCercano(it, distanciaKm(atraccion.latitude, atraccion.longitude, it.latitude, it.longitude)) }
            .minByOrNull { it.distanciaKm }

    fun guardarAtraccion(id: Int) {
        _guardadas.value = _guardadas.value + id
    }

    fun eliminarGuardada(id: Int) {
        _guardadas.value = _guardadas.value - id
    }

    private fun conUbicacion(atraccion: TouristicAttraction): AtraccionConUbicacion {
        val ciudad = datos.ciudades.find { it.id == atraccion.cityId }
        val departamento = datos.departamentos.find { it.id == ciudad?.departmentId }
        val region = datos.regiones.find { it.id == departamento?.regionId }
        return AtraccionConUbicacion(atraccion, ciudad, departamento, region)
    }

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
