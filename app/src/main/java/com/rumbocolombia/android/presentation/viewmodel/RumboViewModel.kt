package com.rumbocolombia.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.rumbocolombia.android.data.dto.Airport
import com.rumbocolombia.android.data.dto.City
import com.rumbocolombia.android.data.dto.Department
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.dto.TouristicAttraction
import com.rumbocolombia.android.data.repository.RumboRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// Lo que necesita una tarjeta: la atracción con el nombre de su ciudad,
// departamento y región ya resueltos.
data class AtraccionConUbicacion(
    val atraccion: TouristicAttraction,
    val ciudad: City?,
    val departamento: Department?,
    val region: Region?
)

// Aeropuerto más cercano a una atracción y a cuántos km queda.
data class AeropuertoCercano(
    val aeropuerto: Airport,
    val distanciaKm: Int
)

class RumboViewModel : ViewModel() {

    private val repositorio = RumboRepository()

    // --- Explorar ---
    val regiones: List<Region> = repositorio.obtenerRegiones()

    private val _regionSeleccionada = MutableStateFlow<Int?>(null) // null = todas
    val regionSeleccionada: StateFlow<Int?> = _regionSeleccionada.asStateFlow()

    private val _atracciones = MutableStateFlow(filtrarPorRegion(null))
    val atracciones: StateFlow<List<AtraccionConUbicacion>> = _atracciones.asStateFlow()

    // --- Buscar ---
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _ciudadesEncontradas = MutableStateFlow<List<City>>(emptyList())
    val ciudadesEncontradas: StateFlow<List<City>> = _ciudadesEncontradas.asStateFlow()

    private val _atraccionesEncontradas = MutableStateFlow<List<AtraccionConUbicacion>>(emptyList())
    val atraccionesEncontradas: StateFlow<List<AtraccionConUbicacion>> = _atraccionesEncontradas.asStateFlow()

    fun seleccionarRegion(regionId: Int?) {
        _regionSeleccionada.value = regionId
        _atracciones.value = filtrarPorRegion(regionId)
    }

    fun buscar(texto: String) {
        _textoBusqueda.value = texto
        val q = texto.trim()
        if (q.isEmpty()) {
            _ciudadesEncontradas.value = emptyList()
            _atraccionesEncontradas.value = emptyList()
            return
        }
        _ciudadesEncontradas.value = repositorio.obtenerCiudades().filter { it.name.contains(q, ignoreCase = true) }
        _atraccionesEncontradas.value = filtrarPorRegion(null).filter {
            it.atraccion.name.contains(q, ignoreCase = true) ||
                it.ciudad?.name?.contains(q, ignoreCase = true) == true
        }
    }

    // --- Detalle ---
    fun obtenerAtraccion(id: Int): AtraccionConUbicacion? =
        repositorio.obtenerAtracciones().find { it.id == id }?.let { conUbicacion(it) }

    fun departamentoDe(ciudad: City): Department? =
        repositorio.obtenerDepartamentos().find { it.id == ciudad.departmentId }

    fun aeropuertoMasCercano(atraccion: TouristicAttraction): AeropuertoCercano? =
        repositorio.obtenerAeropuertos()
            .map { AeropuertoCercano(it, distanciaKm(atraccion.latitude, atraccion.longitude, it.latitude, it.longitude)) }
            .minByOrNull { it.distanciaKm }

    private fun filtrarPorRegion(regionId: Int?): List<AtraccionConUbicacion> =
        repositorio.obtenerAtracciones()
            .map { conUbicacion(it) }
            .filter { regionId == null || it.region?.id == regionId }

    private fun conUbicacion(atraccion: TouristicAttraction): AtraccionConUbicacion {
        val ciudad = repositorio.obtenerCiudades().find { it.id == atraccion.cityId }
        val departamento = repositorio.obtenerDepartamentos().find { it.id == ciudad?.departmentId }
        val region = repositorio.obtenerRegiones().find { it.id == departamento?.regionId }
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
