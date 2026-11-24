package com.rumbocolombia.android.data.local

import com.rumbocolombia.android.data.dto.AtraccionConUbicacion
import com.rumbocolombia.android.data.dto.City
import com.rumbocolombia.android.data.dto.Department
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.dto.TouristicAttraction

// Convierte los datos de la API (DTO) en una fila de la tabla y al revés.
// Así las pantallas siguen usando los mismos modelos, vengan de la API o del celular.

fun AtraccionConUbicacion.aEntity() = AtraccionEntity(
    id = atraccion.id,
    nombre = atraccion.name,
    descripcion = atraccion.description,
    imagen = atraccion.images?.firstOrNull(),
    latitud = atraccion.latitude,
    longitud = atraccion.longitude,
    ciudadId = atraccion.cityId,
    ciudadNombre = ciudad?.name,
    departamentoId = departamento?.id,
    departamentoNombre = departamento?.name,
    regionId = region?.id,
    regionNombre = region?.name
)

fun AtraccionEntity.aModelo(): AtraccionConUbicacion {
    val region = if (regionId != null && regionNombre != null) Region(regionId, regionNombre, null) else null
    val departamento = if (departamentoId != null && departamentoNombre != null) {
        Department(departamentoId, departamentoNombre, null, null, null, null, null, regionId ?: 0, null)
    } else null
    val ciudad = ciudadNombre?.let { City(ciudadId, it, null, null, null, null, departamentoId ?: 0) }
    return AtraccionConUbicacion(
        atraccion = TouristicAttraction(
            id = id,
            name = nombre,
            description = descripcion,
            images = listOfNotNull(imagen),
            latitude = latitud,
            longitude = longitud,
            cityId = ciudadId,
            city = ciudad
        ),
        ciudad = ciudad,
        departamento = departamento,
        region = region
    )
}
