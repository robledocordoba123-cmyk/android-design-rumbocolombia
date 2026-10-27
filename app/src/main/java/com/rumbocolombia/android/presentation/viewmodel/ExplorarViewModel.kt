package com.rumbocolombia.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.repository.TurismoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel de la pantalla Explorar: lista de atracciones y filtro por región.
class ExplorarViewModel : ViewModel() {

    private val repositorio = TurismoRepository

    // Todas las atracciones que llegaron de la API (sin filtrar)
    private var todas: List<AtraccionConUbicacion> = emptyList()

    private val _estado = MutableStateFlow<UiState<List<AtraccionConUbicacion>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<AtraccionConUbicacion>>> = _estado.asStateFlow()

    private val _regiones = MutableStateFlow<List<Region>>(emptyList())
    val regiones: StateFlow<List<Region>> = _regiones.asStateFlow()

    private val _regionSeleccionada = MutableStateFlow<Int?>(null) // null = todas
    val regionSeleccionada: StateFlow<Int?> = _regionSeleccionada.asStateFlow()

    // Ids de las atracciones guardadas, para marcar las tarjetas
    val guardadas: StateFlow<Set<Int>> = repositorio.guardadas

    init {
        cargar()
    }

    // Pide los datos a la API. También lo usa el botón Reintentar.
    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            try {
                _regiones.value = repositorio.obtenerRegiones()
                todas = repositorio.obtenerAtracciones()
                aplicarFiltro()
            } catch (e: Exception) {
                _estado.value = UiState.Error(mensajeDeError(e))
            }
        }
    }

    fun seleccionarRegion(regionId: Int?) {
        _regionSeleccionada.value = regionId
        if (_estado.value is UiState.Exito) aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val regionId = _regionSeleccionada.value
        _estado.value = UiState.Exito(todas.filter { regionId == null || it.region?.id == regionId })
    }
}
