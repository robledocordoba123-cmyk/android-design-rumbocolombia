package com.rumbocolombia.android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object): aquí están las consultas SQL a las tablas.
 * Room genera el código de cada función a partir de su anotación.
 */
@Dao
interface AtraccionDao {

    // --- Tabla atracciones ---

    // REPLACE: si la atracción ya existe, se actualiza con los datos nuevos de la API
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarAtracciones(atracciones: List<AtraccionEntity>)

    @Query("SELECT * FROM atracciones ORDER BY id")
    suspend fun obtenerAtracciones(): List<AtraccionEntity>

    @Query("SELECT * FROM atracciones WHERE id = :id")
    suspend fun obtenerAtraccion(id: Int): AtraccionEntity?

    @Query("SELECT * FROM atracciones WHERE nombre LIKE '%' || :texto || '%' OR ciudadNombre LIKE '%' || :texto || '%'")
    suspend fun buscar(texto: String): List<AtraccionEntity>

    // --- Tabla guardadas ---

    // Flow: cada vez que cambia la tabla, la pantalla recibe la lista nueva
    @Query("SELECT atraccionId FROM guardadas")
    fun observarGuardadas(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardar(guardada: GuardadaEntity)

    @Query("DELETE FROM guardadas WHERE atraccionId = :id")
    suspend fun eliminarGuardada(id: Int)
}
