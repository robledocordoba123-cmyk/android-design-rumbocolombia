package com.rumbocolombia.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import coil.Coil
import com.rumbocolombia.android.data.network.CargadorDeImagenes
import com.rumbocolombia.android.presentation.screens.AppNavigation
import com.rumbocolombia.android.ui.theme.RumboColombiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Coil.setImageLoader(CargadorDeImagenes.crear(this))
        enableEdgeToEdge()
        setContent {
            RumboColombiaTheme {
                AppNavigation()
            }
        }
    }
}
