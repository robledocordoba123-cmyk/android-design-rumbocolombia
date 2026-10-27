package com.rumbocolombia.android.data.network

import android.content.Context
import coil.ImageLoader
import okhttp3.OkHttpClient

// Configuración de Coil para descargar las fotos que manda la API.
// Muchas fotos están en Wikimedia, que rechaza las apps que no se
// identifican, por eso cada petición lleva el nombre de la app.
object CargadorDeImagenes {

    private const val USER_AGENT =
        "RumboColombia/1.0 (https://github.com/robledocordoba123-cmyk/android-design-rumbocolombia)"

    fun crear(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { cadena ->
                        cadena.proceed(
                            cadena.request().newBuilder()
                                .header("User-Agent", USER_AGENT)
                                .build()
                        )
                    }
                    .build()
            }
            .crossfade(true)
            .build()

    // Wikimedia solo entrega miniaturas en algunos anchos (por ejemplo 500 px).
    // La API manda enlaces de 800 px o 1024 px, así que se cambian a 500 px.
    fun ajustarUrl(url: String?): String? =
        url?.replace(Regex("(upload\\.wikimedia\\.org/.+/thumb/.+/)\\d+px-"), "$1500px-")
}
