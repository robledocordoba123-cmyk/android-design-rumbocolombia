package com.rumbocolombia.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla "guardadas": ids de las atracciones que el usuario guardó con el botón Guardar.
 * Antes vivían solo en memoria y se perdían al cerrar la app; ahora quedan en el celular.
 */
@Entity(tableName = "guardadas")
data class GuardadaEntity(
    @PrimaryKey val atraccionId: Int
)
