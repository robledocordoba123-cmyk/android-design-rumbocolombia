package com.rumbocolombia.android

import android.app.Application
import com.rumbocolombia.android.data.local.BaseDeDatos

// Clase Application: se ejecuta una vez al abrir la app, antes que cualquier pantalla.
// Aquí se crea la base de datos de Room para que el repositorio la pueda usar.
class RumboColombiaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        BaseDeDatos.iniciar(this)
    }
}
