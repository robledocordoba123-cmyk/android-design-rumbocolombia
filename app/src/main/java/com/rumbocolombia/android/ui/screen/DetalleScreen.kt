package com.rumbocolombia.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rumbocolombia.android.viewmodel.RumboViewModel

// Pantalla 2: detalle de una atracción. Solo recibe el attractionId.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    viewModel: RumboViewModel,
    attractionId: Int,
    onVolver: () -> Unit
) {
    val item = viewModel.obtenerAtraccion(attractionId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.atraccion?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (item == null) {
            Text("No se encontró la atracción.", modifier = Modifier.padding(paddingValues).padding(16.dp))
            return@Scaffold
        }
        val cercano = viewModel.aeropuertoMasCercano(item.atraccion)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            FotoAtraccion(alto = 220.dp)
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = item.atraccion.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = listOfNotNull(item.ciudad?.name, item.departamento?.name, item.region?.let { "Región ${it.name}" })
                        .joinToString(" · "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(item.atraccion.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ubicación: ${item.atraccion.latitude}, ${item.atraccion.longitude}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (cercano != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Filled.Flight, contentDescription = null)
                            Text("Cómo llegar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${cercano.aeropuerto.name} (${cercano.aeropuerto.iataCode})")
                            Text("${cercano.aeropuerto.type} · a ${cercano.distanciaKm} km en línea recta")
                        }
                    }
                }
            }
        }
    }
}
