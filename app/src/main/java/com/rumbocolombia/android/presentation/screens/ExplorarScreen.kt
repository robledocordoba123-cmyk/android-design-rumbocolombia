package com.rumbocolombia.android.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.presentation.components.Cargando
import com.rumbocolombia.android.presentation.components.ErrorConReintentar
import com.rumbocolombia.android.presentation.components.TarjetaAtraccion
import com.rumbocolombia.android.presentation.viewmodel.ExplorarViewModel
import com.rumbocolombia.android.presentation.viewmodel.UiState
import com.rumbocolombia.android.ui.theme.RumboColombiaTheme

// Pantalla 1: lista de atracciones con filtro por región.
@Composable
fun ExplorarScreen(
    onAbrirDetalle: (attractionId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExplorarViewModel = viewModel()
) {
    // Observar el estado del ViewModel
    val estado by viewModel.estado.collectAsState()
    val regiones by viewModel.regiones.collectAsState()
    val regionSeleccionada by viewModel.regionSeleccionada.collectAsState()
    val guardadas by viewModel.guardadas.collectAsState()

    ExplorarContenido(
        estado = estado,
        regiones = regiones,
        regionSeleccionada = regionSeleccionada,
        guardadas = guardadas,
        onSeleccionarRegion = viewModel::seleccionarRegion,
        onReintentar = viewModel::cargar,
        onAbrirDetalle = onAbrirDetalle,
        modifier = modifier
    )
}

// Lo que se dibuja; no conoce al ViewModel (así se puede ver en el Preview).
@Composable
fun ExplorarContenido(
    estado: UiState<List<AtraccionConUbicacion>>,
    regiones: List<Region>,
    regionSeleccionada: Int?,
    guardadas: Set<Int>,
    onSeleccionarRegion: (Int?) -> Unit,
    onReintentar: () -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Explora Colombia",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )

        // Chips de región (las 6 regiones vienen de GET /Region)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = regionSeleccionada == null,
                    onClick = { onSeleccionarRegion(null) },
                    label = { Text("Todas") }
                )
            }
            items(regiones) { region ->
                FilterChip(
                    selected = regionSeleccionada == region.id,
                    onClick = { onSeleccionarRegion(region.id) },
                    label = { Text(region.name) }
                )
            }
        }

        when (estado) {
            is UiState.Cargando -> Cargando()
            is UiState.Error -> ErrorConReintentar(estado.mensaje, onReintentar)
            is UiState.Exito -> {
                if (estado.datos.isEmpty()) {
                    Text("No hay atracciones en esta región.", modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(estado.datos) { item ->
                            TarjetaAtraccion(
                                item = item,
                                guardada = item.atraccion.id in guardadas,
                                onClick = { onAbrirDetalle(item.atraccion.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Vista previa en Android Studio (botón Split o Design). Como el Preview no
// se conecta a internet, usa una atracción de ejemplo (ver EjemplosPreview.kt).
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExplorarScreenPreview() {
    RumboColombiaTheme {
        ExplorarContenido(
            estado = UiState.Exito(listOf(EjemplosPreview.atraccion)),
            regiones = listOf(EjemplosPreview.region),
            regionSeleccionada = null,
            guardadas = emptySet(),
            onSeleccionarRegion = {},
            onReintentar = {},
            onAbrirDetalle = {}
        )
    }
}

@Preview(showBackground = true, name = "Explorar - cargando")
@Composable
private fun ExplorarCargandoPreview() {
    RumboColombiaTheme {
        ExplorarContenido(UiState.Cargando, emptyList(), null, emptySet(), {}, {}, {})
    }
}

@Preview(showBackground = true, name = "Explorar - error")
@Composable
private fun ExplorarErrorPreview() {
    RumboColombiaTheme {
        ExplorarContenido(UiState.Error("Sin conexión a internet"), emptyList(), null, emptySet(), {}, {}, {})
    }
}
