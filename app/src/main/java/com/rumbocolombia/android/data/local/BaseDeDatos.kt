package com.rumbocolombia.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local de la app (archivo rumbo_colombia.db en el celular).
 * Tiene 2 tablas: atracciones y guardadas.
 */
@Database(
    entities = [AtraccionEntity::class, GuardadaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BaseDeDatos : RoomDatabase() {

    abstract fun atraccionDao(): AtraccionDao

    companion object {
        @Volatile
        private var instancia: BaseDeDatos? = null

        // Una sola base de datos para toda la app (patrón Singleton).
        // Se crea en RumboColombiaApp, al abrir la app.
        fun iniciar(context: Context): BaseDeDatos =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    BaseDeDatos::class.java,
                    "rumbo_colombia.db"
                ).build().also { instancia = it }
            }

        fun obtener(): BaseDeDatos =
            checkNotNull(instancia) { "La base de datos no se ha iniciado" }
    }
}
