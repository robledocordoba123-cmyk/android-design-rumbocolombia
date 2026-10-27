package com.rumbocolombia.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.rumbocolombia.android.data.dto.AeropuertoCercano
import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.repository.TurismoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ViewModel de la pantalla Detalle: recibe el attractionId y prepara la
// atracción, su aeropuerto más cercano y si está guardada.
class DetalleViewModel : ViewModel() {

    private val repositorio = TurismoRepository

    private val _atraccion = MutableStateFlow<AtraccionConUbicacion?>(null)
    val atraccion: StateFlow<AtraccionConUbicacion?> = _atraccion.asStateFlow()

    private val _aeropuertoCercano = MutableStateFlow<AeropuertoCercano?>(null)
    val aeropuertoCercano: StateFlow<AeropuertoCercano?> = _aeropuertoCercano.asStateFlow()

    val guardadas: StateFlow<Set<Int>> = repositorio.guardadas

    fun cargar(attractionId: Int) {
        val item = repositorio.obtenerAtraccion(attractionId)
        _atraccion.value = item
        _aeropuertoCercano.value = item?.let { repositorio.aeropuertoMasCercano(it.atraccion) }
    }

    // Agrega o quita la atracción de las guardadas para ver sin internet
    fun cambiarGuardada(attractionId: Int) {
        if (attractionId in repositorio.guardadas.value) {
            repositorio.eliminarGuardada(attractionId)
        } else {
            repositorio.guardarAtraccion(attractionId)
        }
    }
}
