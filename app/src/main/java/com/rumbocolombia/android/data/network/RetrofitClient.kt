package com.rumbocolombia.android.data.network

import com.rumbocolombia.android.data.api.ApiColombiaService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Configuración de Retrofit: la URL base de la API y Gson para convertir
// el JSON en las data class de data/dto. API Colombia no usa API Key.
object RetrofitClient {

    private const val URL_BASE = "https://api-colombia.com/api/v1/"

    val api: ApiColombiaService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiColombiaService::class.java)
    }
}
