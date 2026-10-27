package com.rumbocolombia.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil.Coil
import com.rumbocolombia.android.data.network.CargadorDeImagenes
import com.rumbocolombia.android.presentation.screens.BuscarScreen
import com.rumbocolombia.android.presentation.screens.DetalleScreen
import com.rumbocolombia.android.presentation.screens.ExplorarScreen
import com.rumbocolombia.android.ui.theme.RumboColombiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Coil.setImageLoader(CargadorDeImagenes.crear(this))
        enableEdgeToEdge()
        setContent {
            RumboColombiaTheme {
                RumboApp()
            }
        }
    }
}

enum class Pestana { EXPLORAR, BUSCAR }

// Navegación simple con estado: la barra inferior cambia entre Explorar y
// Buscar, y al tocar una atracción se abre el Detalle con su attractionId.
@Composable
fun RumboApp() {
    var pestana by remember { mutableStateOf(Pestana.EXPLORAR) }
    var attractionId by remember { mutableStateOf<Int?>(null) }

    val idAbierto = attractionId
    if (idAbierto != null) {
        BackHandler { attractionId = null }
        DetalleScreen(
            attractionId = idAbierto,
            onVolver = { attractionId = null }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pestana == Pestana.EXPLORAR,
                    onClick = { pestana = Pestana.EXPLORAR },
                    icon = { Icon(Icons.Filled.Explore, contentDescription = null) },
                    label = { Text("Explorar") }
                )
                NavigationBarItem(
                    selected = pestana == Pestana.BUSCAR,
                    onClick = { pestana = Pestana.BUSCAR },
                    icon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    label = { Text("Buscar") }
                )
            }
        }
    ) { innerPadding ->
        val abrirDetalle: (Int) -> Unit = { attractionId = it }
        when (pestana) {
            Pestana.EXPLORAR -> ExplorarScreen(abrirDetalle, Modifier.padding(innerPadding))
            Pestana.BUSCAR -> BuscarScreen(abrirDetalle, Modifier.padding(innerPadding))
        }
    }
}
