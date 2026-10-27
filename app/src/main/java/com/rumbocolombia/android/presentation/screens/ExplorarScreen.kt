package com.rumbocolombia.android.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rumbocolombia.android.presentation.components.TarjetaAtraccion
import com.rumbocolombia.android.presentation.viewmodel.ExplorarViewModel

// Pantalla 1: lista de atracciones con filtro por región.
@Composable
fun ExplorarScreen(
    onAbrirDetalle: (attractionId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExplorarViewModel = viewModel()
) {
    // Observar el estado del ViewModel
    val atracciones by viewModel.atracciones.collectAsState()
    val regionSeleccionada by viewModel.regionSeleccionada.collectAsState()
    val guardadas by viewModel.guardadas.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Explora Colombia",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Chips de región
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = regionSeleccionada == null,
                        onClick = { viewModel.seleccionarRegion(null) },
                        label = { Text("Todas") }
                    )
                }
                items(viewModel.regiones) { region ->
                    FilterChip(
                        selected = regionSeleccionada == region.id,
                        onClick = { viewModel.seleccionarRegion(region.id) },
                        label = { Text(region.name) }
                    )
                }
            }
        }

        if (atracciones.isEmpty()) {
            item { Text("No hay atracciones en esta región.") }
        } else {
            items(atracciones) { item ->
                TarjetaAtraccion(
                    item = item,
                    guardada = item.atraccion.id in guardadas,
                    onClick = { onAbrirDetalle(item.atraccion.id) }
                )
            }
        }
    }
}
