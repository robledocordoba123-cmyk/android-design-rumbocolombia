package com.rumbocolombia.android.data.dto

// Modelo de datos de la app. Son las mismas 5 entidades del diagrama
// entidad-relación (README, sección 2) y los campos se llaman igual que
// en el JSON de API Colombia, así Gson los llena directamente.

data class Region(
    val id: Int,
    val name: String,
    val description: String?
)

data class Department(
    val id: Int,
    val name: String,
    val description: String?,
    val population: Int?,
    val surface: Int?,
    val municipalities: Int?,
    val phonePrefix: String?,
    val regionId: Int,       // Región a la que pertenece
    val cityCapitalId: Int?  // Ciudad capital
)

data class City(
    val id: Int,
    val name: String,
    val description: String?,
    val population: Int?,    // La mayoría de ciudades lo traen vacío
    val surface: Int?,       // La mayoría de ciudades lo traen vacío
    val postalCode: String?,
    val departmentId: Int
)

data class TouristicAttraction(
    val id: Int,
    val name: String,
    val description: String?,
    val images: List<String>?,
    val latitude: Double,    // En el JSON viene como texto; Gson lo convierte
    val longitude: Double,
    val cityId: Int,
    val city: City?          // La API manda la ciudad dentro de la atracción
)

data class Airport(
    val id: Int,
    val name: String,
    val iataCode: String?,
    val oaciCode: String?,
    val type: String?,
    val latitude: Double,
    val longitude: Double,
    val cityId: Int
)

// Lo que necesita una tarjeta: la atracción con su ciudad, departamento
// y región ya resueltos a partir de los ids.
data class AtraccionConUbicacion(
    val atraccion: TouristicAttraction,
    val ciudad: City?,
    val departamento: Department?,
    val region: Region?
)

// Aeropuerto más cercano a una atracción y a cuántos km queda.
data class AeropuertoCercano(
    val aeropuerto: Airport,
    val distanciaKm: Int
)

// Todo lo que muestra la pantalla Detalle.
data class DetalleAtraccion(
    val item: AtraccionConUbicacion,
    val aeropuertoCercano: AeropuertoCercano?
)

// Resultados de la pantalla Buscar.
data class ResultadosBusqueda(
    val ciudades: List<City>,
    val atracciones: List<AtraccionConUbicacion>
)
