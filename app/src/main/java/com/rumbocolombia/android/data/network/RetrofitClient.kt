package com.rumbocolombia.android.data.network

import com.rumbocolombia.android.data.api.ApiClient
import com.rumbocolombia.android.data.api.ApiColombiaService

// Mantiene el acceso por el paquete data.network. La configuración de
// Retrofit, Kotlinx Serialization y OkHttp está en data/api/ApiClient.kt.
object RetrofitClient {
    val api: ApiColombiaService get() = ApiClient.apiColombiaService
}
