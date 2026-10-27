package com.rumbocolombia.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumbocolombia.android.data.dto.ResultadosBusqueda
import com.rumbocolombia.android.data.repository.TurismoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel de la pantalla Buscar: pide a la API ciudades y atracciones.
class BuscarViewModel : ViewModel() {

    private val repositorio = TurismoRepository

    // null = todavía no se ha escrito nada
    private val _estado = MutableStateFlow<UiState<ResultadosBusqueda>?>(null)
    val estado: StateFlow<UiState<ResultadosBusqueda>?> = _estado.asStateFlow()

    // Nombre del departamento de cada ciudad encontrada (id ciudad -> nombre)
    private val _departamentos = MutableStateFlow<Map<Int, String>>(emptyMap())
    val departamentos: StateFlow<Map<Int, String>> = _departamentos.asStateFlow()

    private var busqueda: Job? = null
    private var ultimoTexto = ""

    fun buscar(texto: String) {
        ultimoTexto = texto.trim()
        // Si el usuario sigue escribiendo, se cancela la búsqueda anterior
        busqueda?.cancel()
        if (ultimoTexto.length < 2) {
            _estado.value = null
            return
        }
        busqueda = viewModelScope.launch {
            delay(400) // Espera a que deje de escribir para no llamar la API en cada letra
            _estado.value = UiState.Cargando
            try {
                val resultados = repositorio.buscar(ultimoTexto)
                _departamentos.value = resultados.ciudades.associate { ciudad ->
                    ciudad.id to (repositorio.departamentoDe(ciudad)?.name ?: "")
                }
                _estado.value = UiState.Exito(resultados)
            } catch (e: CancellationException) {
                throw e // Búsqueda cancelada porque el usuario siguió escribiendo: no es un error
            } catch (e: Exception) {
                _estado.value = UiState.Error(mensajeDeError(e))
            }
        }
    }

    fun reintentar() = buscar(ultimoTexto)
}
