package com.cibertec.serfagab.data

import android.content.Context
import android.database.DatabaseUtils
import com.cibertec.serfagab.data.store.TipoMaterialStore
import com.cibertec.serfagab.model.TipoMaterial

object DatosPrueba {

    fun cargarSiVacio(context: Context) {
        cargarUsuario(context)
        cargarTiposMaterial(context)
    }

    private fun cargarUsuario(context: Context) {
        val db = SerfagabDbHelper(context.applicationContext).writableDatabase
        if (DatabaseUtils.queryNumEntries(db, SerfagabContrato.TABLA_USUARIO) > 0) return

        db.beginTransaction()
        try {
            db.execSQL(
                "INSERT INTO ${SerfagabContrato.TABLA_TIPOU} "+
                "(${SerfagabContrato.COLUMNA_TIPOU_DESCRIPCION}) VALUES ('Administrador')"
            )
            db.execSQL(
                "INSERT INTO ${SerfagabContrato.TABLA_USUARIO} (" +
                        "${SerfagabContrato.COLUMNA_USUARIO_ID_TIPO}, " +
                        "${SerfagabContrato.COLUMNA_USUARIO_NOMBRES}, " +
                        "${SerfagabContrato.COLUMNA_USUARIO_APELLIDOS}, " +
                        "${SerfagabContrato.COLUMNA_USUARIO_EMAIL}, " +
                        "${SerfagabContrato.COLUMNA_USUARIO_LOGIN}, " +
                        "${SerfagabContrato.COLUMNA_USUARIO_CLAVE}, " +
                        "${SerfagabContrato.COLUMNA_USUARIO_ACTIVO}) " +
                        "VALUES (1, 'Juan', 'Perez' , 'juan@serfagab.com', 'admin', '1234', 1)"
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun cargarTiposMaterial(context: Context) {
        val store = TipoMaterialStore(context)
        if (store.obtenerTodos().isNotEmpty()) return

        store.insertar(TipoMaterial(nombre = "Plancha", descripcion = "Son materiales metalicos para tablero", activo = true))
        store.insertar(TipoMaterial(nombre = "Pintura", descripcion = "Gris ANSI 61", activo = true))
        store.insertar(TipoMaterial(nombre = "Tornilleria", descripcion = "Tornillos, tuercas y arandelas", activo = true))
        store.insertar(TipoMaterial(nombre = "Cable electrico", descripcion = "Conductores para tableros e instalaciones", activo = true))
        store.insertar(TipoMaterial(nombre = "Herramienta manual", descripcion = "Herramientas de taller y montaje", activo = false))

    }

}