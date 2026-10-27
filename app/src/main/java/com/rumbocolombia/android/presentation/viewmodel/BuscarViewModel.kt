package com.rumbocolombia.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.dto.City
import com.rumbocolombia.android.data.dto.Department
import com.rumbocolombia.android.data.repository.TurismoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ViewModel de la pantalla Buscar: resultados de ciudades y atracciones.
class BuscarViewModel : ViewModel() {

    private val repositorio = TurismoRepository

    private val _ciudades = MutableStateFlow<List<City>>(emptyList())
    val ciudades: StateFlow<List<City>> = _ciudades.asStateFlow()

    private val _atracciones = MutableStateFlow<List<AtraccionConUbicacion>>(emptyList())
    val atracciones: StateFlow<List<AtraccionConUbicacion>> = _atracciones.asStateFlow()

    fun buscar(texto: String) {
        val q = texto.trim()
        if (q.isEmpty()) {
            _ciudades.value = emptyList()
            _atracciones.value = emptyList()
            return
        }
        _ciudades.value = repositorio.buscarCiudades(q)
        _atracciones.value = repositorio.buscarAtracciones(q)
    }

    fun departamentoDe(ciudad: City): Department? = repositorio.departamentoDe(ciudad)
}
