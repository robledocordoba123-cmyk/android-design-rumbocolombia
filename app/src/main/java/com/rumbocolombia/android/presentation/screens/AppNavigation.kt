package com.rumbocolombia.android.presentation.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun AppNavigation() {
    // 1. Instanciar el controlador de navegación
    val navController = rememberNavController()

    // 2. Definir el NavHost especificando la pantalla inicial
    NavHost(
        navController = navController,
        startDestination = ExplorarScreen
    ) {
        // Registro de la pantalla principal
        composable<ExplorarScreen> {
            ExplorarScreen(
                onNavigateToDetalle = { attractionId ->
                    // Navegar pasando una instancia de la data class
                    navController.navigate(DetalleScreen(attractionId = attractionId))
                },
                onNavigateToBuscar = {
                    // Navegar al objeto estático
                    navController.navigate(BuscarScreen)
                }
            )
        }

        // Registro de la pantalla de detalle con extracción de argumentos
        composable<DetalleScreen> { backStackEntry ->
            // toRoute() recupera automáticamente los valores pasados
            val detalleArgs: DetalleScreen = backStackEntry.toRoute()

            DetalleScreen(
                attractionId = detalleArgs.attractionId,
                onBack = {
                    navController.popBackStack() // Elimina la vista actual del stack
                }
            )
        }

        // Registro de la pantalla de búsqueda
        composable<BuscarScreen> {
            BuscarScreen(
                onNavigateToDetalle = { attractionId ->
                    navController.navigate(DetalleScreen(attractionId = attractionId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
