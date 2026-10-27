package com.rumbocolombia.android.presentation.screens

import com.rumbocolombia.android.data.dto.AeropuertoCercano
import com.rumbocolombia.android.data.dto.Airport
import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.dto.City
import com.rumbocolombia.android.data.dto.Department
import com.rumbocolombia.android.data.dto.DetalleAtraccion
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.dto.TouristicAttraction

// SOLO para las vistas previas (@Preview) de Android Studio, que no pueden
// conectarse a internet. La app nunca usa estos datos: todo lo que se ve
// al ejecutarla viene de API Colombia.
internal object EjemplosPreview {

    val region = Region(5, "Andina", null)
    private val departamento = Department(5, "Bogotá", null, null, null, null, null, 5, 167)
    val ciudad = City(167, "Bogotá D.C.", null, 7901653, 1636, "110110", 5)

    val atraccion = AtraccionConUbicacion(
        atraccion = TouristicAttraction(
            1, "Museo del oro",
            "Museo del Banco de la República con piezas de orfebrería precolombina.",
            emptyList(), 4.601919, -74.072, 167, ciudad
        ),
        ciudad = ciudad,
        departamento = departamento,
        region = region
    )

    val detalle = DetalleAtraccion(
        item = atraccion,
        aeropuertoCercano = AeropuertoCercano(
            Airport(3, "Aeropuerto Internacional El Dorado", "BOG", "SKBO", "Internacional", 4.69, -74.16, 167),
            14
        )
    )
}
