package com.rumbocolombia.android.presentation.viewmodel

// Los 3 estados de cada pantalla (README, sección 4 "Estados de cada pantalla").
sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val datos: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}

// Mensaje claro para el usuario cuando falla la petición.
fun mensajeDeError(e: Exception): String =
    if (e is java.io.IOException) "Sin conexión a internet" else "No se pudieron cargar los datos"
