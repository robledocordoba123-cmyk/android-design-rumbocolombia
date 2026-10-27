package com.rumbocolombia.android.data.api

import com.rumbocolombia.android.data.dto.Airport
import com.rumbocolombia.android.data.dto.City
import com.rumbocolombia.android.data.dto.Department
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.dto.TouristicAttraction
import retrofit2.http.GET
import retrofit2.http.Path

// Los endpoints de API Colombia que usa la app (tabla de la sección 4 del README).
// Cada función es suspend: se llama desde una corrutina y no bloquea la pantalla.
interface ApiColombiaService {

    @GET("TouristicAttraction")
    suspend fun obtenerAtracciones(): List<TouristicAttraction>

    @GET("TouristicAttraction/{id}")
    suspend fun obtenerAtraccion(@Path("id") id: Int): TouristicAttraction

    @GET("TouristicAttraction/search/{keyword}")
    suspend fun buscarAtracciones(@Path("keyword") texto: String): List<TouristicAttraction>

    @GET("City/search/{keyword}")
    suspend fun buscarCiudades(@Path("keyword") texto: String): List<City>

    @GET("Region")
    suspend fun obtenerRegiones(): List<Region>

    @GET("Department")
    suspend fun obtenerDepartamentos(): List<Department>

    @GET("Airport")
    suspend fun obtenerAeropuertos(): List<Airport>
}
