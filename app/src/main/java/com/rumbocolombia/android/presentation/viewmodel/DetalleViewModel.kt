package com.rumbocolombia.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumbocolombia.android.data.dto.DetalleAtraccion
import com.rumbocolombia.android.data.repository.TurismoRepository
import com.rumbocolombia.android.data.repository.TurismoRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel de la pantalla Detalle: recibe el attractionId y pide a la API
// la atracción y el aeropuerto más cercano.
class DetalleViewModel(
    private val repositorio: TurismoRepository = TurismoRepositoryImpl()
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<DetalleAtraccion>>(UiState.Cargando)
    val estado: StateFlow<UiState<DetalleAtraccion>> = _estado.asStateFlow()

    // Ids guardados en Room: el botón Guardar cambia apenas cambia la tabla
    val guardadas: StateFlow<Set<Int>> = repositorio.guardadas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    fun cargar(attractionId: Int) {
        viewModelScope.launch {
            _estado.value = UiState.Cargando

            repositorio.obtenerDetalle(attractionId)
                .onSuccess { detalle ->
                    _estado.value = UiState.Exito(detalle)
                }
                .onFailure { error ->
                    _estado.value = UiState.Error(mensajeDeError(error))
                }
        }
    }

    // Agrega o quita la atracción de las guardadas (se guarda en Room)
    fun cambiarGuardada(attractionId: Int) {
        viewModelScope.launch {
            if (attractionId in guardadas.value) {
                repositorio.eliminarGuardada(attractionId)
            } else {
                repositorio.guardarAtraccion(attractionId)
            }
        }
    }
}
