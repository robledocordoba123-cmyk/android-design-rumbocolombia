package com.rumbocolombia.android.presentation.screens

import androidx.compose.foundation.layout.Column
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
import com.rumbocolombia.android.data.dto.ResultadosBusqueda
import com.rumbocolombia.android.presentation.components.Cargando
import com.rumbocolombia.android.presentation.components.ErrorConReintentar
import com.rumbocolombia.android.presentation.components.FilaResultado
import com.rumbocolombia.android.presentation.viewmodel.BuscarViewModel
import com.rumbocolombia.android.presentation.viewmodel.UiState
import com.rumbocolombia.android.ui.theme.RumboColombiaTheme

// Pantalla 3: buscar ciudades y atracciones por nombre en la API.
@Composable
fun BuscarScreen(
    onAbrirDetalle: (attractionId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BuscarViewModel = viewModel()
) {
    // Estado temporal de la pantalla: lo que el usuario va escribiendo
    var texto by remember { mutableStateOf("") }

    // Resultados que prepara el ViewModel
    val estado by viewModel.estado.collectAsState()
    val departamentos by viewModel.departamentos.collectAsState()

    BuscarContenido(
        texto = texto,
        onTextoCambia = {
            texto = it
            viewModel.buscar(it)
        },
        estado = estado,
        departamentos = departamentos,
        onReintentar = viewModel::reintentar,
        onAbrirDetalle = onAbrirDetalle,
        modifier = modifier
    )
}

@Composable
fun BuscarContenido(
    texto: String,
    onTextoCambia: (String) -> Unit,
    estado: UiState<ResultadosBusqueda>?,
    departamentos: Map<Int, String>,
    onReintentar: () -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            text = "Buscar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        OutlinedTextField(
            value = texto,
            onValueChange = onTextoCambia,
            label = { Text("Ciudad o atracción") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        when (estado) {
            null -> Text(
                "Escribe al menos 2 letras para buscar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            is UiState.Cargando -> Cargando()
            is UiState.Error -> ErrorConReintentar(estado.mensaje, onReintentar)
            is UiState.Exito -> {
                val ciudades = estado.datos.ciudades
                val atracciones = estado.datos.atracciones
                if (ciudades.isEmpty() && atracciones.isEmpty()) {
                    Text("No se encontraron resultados para \"$texto\".")
                }
                LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                    if (ciudades.isNotEmpty()) {
                        item { Titulo("Ciudades") }
                        items(ciudades) { ciudad ->
                            val departamento = departamentos[ciudad.id] ?: ""
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
                                fotoUrl = item.atraccion.images?.firstOrNull(),
                                onClick = { onAbrirDetalle(item.atraccion.id) }
                            )
                        }
                    }
                }
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

// Vista previa en Android Studio con un resultado de ejemplo (sin internet)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BuscarScreenPreview() {
    RumboColombiaTheme {
        BuscarContenido(
            texto = "bogo",
            onTextoCambia = {},
            estado = UiState.Exito(
                ResultadosBusqueda(listOf(EjemplosPreview.ciudad), listOf(EjemplosPreview.atraccion))
            ),
            departamentos = mapOf(EjemplosPreview.ciudad.id to "Bogotá"),
            onReintentar = {},
            onAbrirDetalle = {}
        )
    }
}
