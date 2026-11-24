package com.rumbocolombia.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla "atracciones": copia en el celular de las atracciones que llegaron de la API.
 * Guarda la atracción con su ubicación (ciudad, departamento y región) para poder
 * mostrar Explorar, Detalle y Buscar aunque no haya internet.
 */
@Entity(tableName = "atracciones")
data class AtraccionEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val descripcion: String?,
    val imagen: String?,
    val latitud: Double,
    val longitud: Double,
    val ciudadId: Int,
    val ciudadNombre: String?,
    val departamentoId: Int?,
    val departamentoNombre: String?,
    val regionId: Int?,
    val regionNombre: String?
)
