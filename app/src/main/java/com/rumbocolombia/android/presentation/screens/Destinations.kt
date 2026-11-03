package com.rumbocolombia.android.presentation.screens

import kotlinx.serialization.Serializable

/**
 * RUTAS TYPE-SAFE
 */

// 1. Ruta sin parámetros: Objeto Kotlin único
@Serializable
object ExplorarScreen

// 2. Ruta con parámetros: Clase de datos (Data Class)
@Serializable
data class DetalleScreen(
    val attractionId: Int
)

// 3. Ruta sin parámetros
@Serializable
object BuscarScreen
