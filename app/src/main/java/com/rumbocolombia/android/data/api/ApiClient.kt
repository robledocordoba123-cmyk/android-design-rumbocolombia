package com.rumbocolombia.android.data.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {

    /** URL Base de API Colombia (no usa API Key) */
    private const val BASE_URL = "https://api-colombia.com/api/v1/"

    /** Configuración del motor de serialización JSON de Kotlin */
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true // Ignora campos adicionales que no estén en el DTO
        coerceInputValues = true // Asigna valores por defecto ante nulos inesperados
        isLenient = true // Acepta JSONs flexibles
        explicitNulls = false // Omite llaves con valor nulo en las peticiones
        encodeDefaults = false // No envía valores por defecto en las peticiones
    }

    /** Interceptor para imprimir logs detallados de red en el Logcat */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /** Cliente OkHttp personalizado */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /** Instancia principal de Retrofit */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    /** Exposición del servicio de API Colombia */
    val apiColombiaService: ApiColombiaService by lazy {
        retrofit.create(ApiColombiaService::class.java)
    }
}
