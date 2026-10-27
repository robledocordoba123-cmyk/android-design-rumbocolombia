package com.rumbocolombia.android.data.dto

// Modelo de datos de la app. Son las mismas 5 entidades del diagrama
// entidad-relación (README, sección 2) y los campos se llaman igual que
// en el JSON de API Colombia para que el mapeo sea directo.

data class Region(
    val id: Int,
    val name: String,
    val description: String
)

data class Department(
    val id: Int,
    val name: String,
    val description: String,
    val population: Int,
    val surface: Int,
    val municipalities: Int,
    val phonePrefix: String,
    val regionId: Int,       // Región a la que pertenece
    val cityCapitalId: Int   // Ciudad capital
)

data class City(
    val id: Int,
    val name: String,
    val description: String,
    val population: Int?,    // Puede venir vacío en la API
    val surface: Int?,       // Puede venir vacío en la API
    val postalCode: String,
    val departmentId: Int
)

data class TouristicAttraction(
    val id: Int,
    val name: String,
    val description: String,
    val images: List<String>,
    val latitude: Double,
    val longitude: Double,
    val cityId: Int
)

data class Airport(
    val id: Int,
    val name: String,
    val iataCode: String,
    val oaciCode: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val cityId: Int
)
