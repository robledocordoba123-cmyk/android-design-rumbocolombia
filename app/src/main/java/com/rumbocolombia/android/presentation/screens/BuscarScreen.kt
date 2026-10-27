package com.rumbocolombia.android.presentation.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rumbocolombia.android.presentation.components.FilaResultado
import com.rumbocolombia.android.presentation.viewmodel.BuscarViewModel
import com.rumbocolombia.android.ui.theme.RumboColombiaTheme

// Pantalla 3: buscar ciudades y atracciones por nombre.
@Composable
fun BuscarScreen(
    onAbrirDetalle: (attractionId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BuscarViewModel = viewModel()
) {
    // Estado temporal de la pantalla: lo que el usuario va escribiendo
    var texto by remember { mutableStateOf("") }

    // Resultados que prepara el ViewModel
    val ciudades by viewModel.ciudades.collectAsState()
    val atracciones by viewModel.atracciones.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Buscar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = texto,
                onValueChange = {
                    texto = it
                    viewModel.buscar(it) // Cada letra pide nuevos resultados
                },
                label = { Text("Ciudad o atracción") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }

        if (texto.isNotBlank() && ciudades.isEmpty() && atracciones.isEmpty()) {
            item { Text("No se encontraron resultados para \"$texto\".") }
        }

        if (ciudades.isNotEmpty()) {
            item { Titulo("Ciudades") }
            items(ciudades) { ciudad ->
                val departamento = viewModel.departamentoDe(ciudad)?.name ?: ""
                val poblacion = ciudad.population?.let { " · $it habitantes" } ?: ""
                FilaResultado(titulo = ciudad.name, subtitulo = departamento + poblacion)
            }
        }

        if (atracciones.isNotEmpty()) {
            item { Titulo("Atracciones") }
            items(atracciones) { item ->
                FilaResultado(
                    titulo = item.atraccion.name,
                    subtitulo = listOfNotNull(item.ciudad?.name, item.departamento?.name).joinToString(", "),
                    onClick = { onAbrirDetalle(item.atraccion.id) }
                )
            }
        }
    }
}

@Composable
private fun Titulo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

// Vista previa en Android Studio (botón Split o Design), sin abrir el emulador
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BuscarScreenPreview() {
    RumboColombiaTheme {
        BuscarScreen(onAbrirDetalle = {}, viewModel = BuscarViewModel())
    }
}
