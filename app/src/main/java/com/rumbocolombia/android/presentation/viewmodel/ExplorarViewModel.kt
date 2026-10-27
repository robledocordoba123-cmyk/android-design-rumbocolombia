package com.rumbocolombia.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.repository.TurismoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ViewModel de la pantalla Explorar: lista de atracciones y filtro por región.
class ExplorarViewModel : ViewModel() {

    private val repositorio = TurismoRepository

    val regiones: List<Region> = repositorio.obtenerRegiones()

    private val _regionSeleccionada = MutableStateFlow<Int?>(null) // null = todas
    val regionSeleccionada: StateFlow<Int?> = _regionSeleccionada.asStateFlow()

    private val _atracciones = MutableStateFlow(repositorio.obtenerAtracciones())
    val atracciones: StateFlow<List<AtraccionConUbicacion>> = _atracciones.asStateFlow()

    // Ids de las atracciones guardadas, para marcar las tarjetas
    val guardadas: StateFlow<Set<Int>> = repositorio.guardadas

    fun seleccionarRegion(regionId: Int?) {
        _regionSeleccionada.value = regionId
        _atracciones.value = repositorio.obtenerAtracciones(regionId)
    }
}
