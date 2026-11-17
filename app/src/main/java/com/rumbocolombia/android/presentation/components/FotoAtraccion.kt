package com.rumbocolombia.android.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.rumbocolombia.android.data.network.CargadorDeImagenes

// Foto de la atracción descargada desde la URL que manda la API (Coil).
// Mientras carga, o si no hay foto, se ve un ícono de paisaje.
@Composable
fun FotoAtraccion(url: String?, modifier: Modifier = Modifier, alto: Dp = 160.dp) {
    val mods = modifier
        .fillMaxWidth()
        .height(alto)
    SubcomposeAsyncImage(
        model = CargadorDeImagenes.ajustarUrl(url),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = mods,
        loading = { IconoPaisaje(Modifier.fillMaxSize()) },
        error = { IconoPaisaje(Modifier.fillMaxSize()) }
    )
}

@Composable
private fun IconoPaisaje(modifier: Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Landscape,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(40.dp)
        )
    }
}
