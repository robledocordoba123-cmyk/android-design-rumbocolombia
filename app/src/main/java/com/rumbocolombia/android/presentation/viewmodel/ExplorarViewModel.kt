package com.rumbocolombia.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.repository.TurismoRepository
import com.rumbocolombia.android.data.repository.TurismoRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel de la pantalla Explorar: lista de atracciones y filtro por región.
class ExplorarViewModel(
    private val repositorio: TurismoRepository = TurismoRepositoryImpl()
) : ViewModel() {

    // Todas las atracciones que llegaron de la API (sin filtrar)
    private var todas: List<AtraccionConUbicacion> = emptyList()

    private val _estado = MutableStateFlow<UiState<List<AtraccionConUbicacion>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<AtraccionConUbicacion>>> = _estado.asStateFlow()

    private val _regiones = MutableStateFlow<List<Region>>(emptyList())
    val regiones: StateFlow<List<Region>> = _regiones.asStateFlow()

    private val _regionSeleccionada = MutableStateFlow<Int?>(null) // null = todas
    val regionSeleccionada: StateFlow<Int?> = _regionSeleccionada.asStateFlow()

    // Ids de las atracciones guardadas (tabla de Room), para marcar las tarjetas
    val guardadas: StateFlow<Set<Int>> = repositorio.guardadas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        cargar()
    }

    // Pide los datos a la API. También lo usa el botón Reintentar.
    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando

            // Llamada al repositorio que retorna Result<List<Region>>
            repositorio.obtenerRegiones()
                .onSuccess { listaObtenida ->
                    _regiones.value = listaObtenida
                }
                .onFailure { error ->
                    _estado.value = UiState.Error(mensajeDeError(error))
                    return@launch
                }

            repositorio.obtenerAtracciones()
                .onSuccess { listaObtenida ->
                    todas = listaObtenida
                    aplicarFiltro()
                }
                .onFailure { error ->
                    _estado.value = UiState.Error(mensajeDeError(error))
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
