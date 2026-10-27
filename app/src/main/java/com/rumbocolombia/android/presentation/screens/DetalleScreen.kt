package com.rumbocolombia.android.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rumbocolombia.android.data.dto.DetalleAtraccion
import com.rumbocolombia.android.presentation.components.Cargando
import com.rumbocolombia.android.presentation.components.ErrorConReintentar
import com.rumbocolombia.android.presentation.components.FotoAtraccion
import com.rumbocolombia.android.presentation.viewmodel.DetalleViewModel
import com.rumbocolombia.android.presentation.viewmodel.UiState
import com.rumbocolombia.android.ui.theme.RumboColombiaTheme

// Pantalla 2: detalle de una atracción. Solo recibe el attractionId.
@Composable
fun DetalleScreen(
    attractionId: Int,
    onVolver: () -> Unit,
    viewModel: DetalleViewModel = viewModel()
) {
    // Cuando llega un attractionId nuevo, el ViewModel pide sus datos a la API
    LaunchedEffect(attractionId) { viewModel.cargar(attractionId) }

    val estado by viewModel.estado.collectAsState()
    val guardadas by viewModel.guardadas.collectAsState()

    DetalleContenido(
        estado = estado,
        estaGuardada = attractionId in guardadas,
        onVolver = onVolver,
        onReintentar = { viewModel.cargar(attractionId) },
        onCambiarGuardada = { viewModel.cambiarGuardada(attractionId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleContenido(
    estado: UiState<DetalleAtraccion>,
    estaGuardada: Boolean,
    onVolver: () -> Unit,
    onReintentar: () -> Unit,
    onCambiarGuardada: () -> Unit
) {
    val titulo = (estado as? UiState.Exito)?.datos?.item?.atraccion?.name ?: "Detalle"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        val mod = Modifier.padding(paddingValues)
        when (estado) {
            is UiState.Cargando -> Cargando(mod)
            is UiState.Error -> ErrorConReintentar(estado.mensaje, onReintentar, mod)
            is UiState.Exito -> {
                val actual = estado.datos.item
                val cercano = estado.datos.aeropuertoCercano
                Column(
                    modifier = mod
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    FotoAtraccion(url = actual.atraccion.images?.firstOrNull(), alto = 240.dp)
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = actual.atraccion.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = listOfNotNull(actual.ciudad?.name, actual.departamento?.name, actual.region?.let { "Región ${it.name}" })
                                .joinToString(" · "),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(actual.atraccion.description ?: "", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ubicación: ${actual.atraccion.latitude}, ${actual.atraccion.longitude}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón Guardar / Quitar de guardadas
                        if (estaGuardada) {
                            OutlinedButton(onClick = onCambiarGuardada, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Filled.Bookmark, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardada · tocar para quitar")
                            }
                        } else {
                            Button(onClick = onCambiarGuardada, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Filled.BookmarkBorder, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar sin internet")
                            }
                        }

                        cercano?.let { c ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Icon(Icons.Filled.Flight, contentDescription = null)
                                    Text("Cómo llegar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("${c.aeropuerto.name} (${c.aeropuerto.iataCode ?: "—"})")
                                    Text("${c.aeropuerto.type ?: "Aeropuerto"} · a ${c.distanciaKm} km en línea recta")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Vista previa en Android Studio con una atracción de ejemplo (sin internet)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetalleScreenPreview() {
    RumboColombiaTheme {
        DetalleContenido(
            estado = UiState.Exito(EjemplosPreview.detalle),
            estaGuardada = false,
            onVolver = {},
            onReintentar = {},
            onCambiarGuardada = {}
        )
    }
}
