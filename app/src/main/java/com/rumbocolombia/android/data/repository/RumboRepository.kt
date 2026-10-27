package com.rumbocolombia.android.data.repository

import com.rumbocolombia.android.data.dto.Airport
import com.rumbocolombia.android.data.dto.City
import com.rumbocolombia.android.data.dto.Department
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.dto.TouristicAttraction

// El Repositorio es el "jefe" de la capa de datos: el ViewModel le pide los
// datos a él y no sabe de dónde salen. Por ahora los saca de DatosDeEjemplo;
// en la sesión de Retrofit solo se cambia esta clase para llamar a la API.
class RumboRepository {

    fun obtenerRegiones(): List<Region> = DatosDeEjemplo.regiones

    fun obtenerDepartamentos(): List<Department> = DatosDeEjemplo.departamentos

    fun obtenerCiudades(): List<City> = DatosDeEjemplo.ciudades

    fun obtenerAtracciones(): List<TouristicAttraction> = DatosDeEjemplo.atracciones

    fun obtenerAeropuertos(): List<Airport> = DatosDeEjemplo.aeropuertos
}
