package com.cibertec.serfagab.data

import android.content.Context
import android.database.DatabaseUtils
import com.cibertec.serfagab.data.store.ProveedorStore
import com.cibertec.serfagab.data.store.TipoMaterialStore
import com.cibertec.serfagab.model.Proveedor
import com.cibertec.serfagab.model.TipoMaterial

object DatosPrueba {

    fun cargarSiVacio(context: Context) {
        cargarUsuario(context)
        cargarTiposMaterial(context)
        cargarProveedores(context)
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

    private fun cargarProveedores(context: Context) {
        val store = ProveedorStore(context)
        if (store.obtenerTodos().isNotEmpty()) return

        store.insertar(Proveedor(razonSocial = "Comercializadora Andina EIRL", ruc = "20605478931",
            celular = "987456321", email = "ventas@andina.com.pe", descripcion = "Varillas y perfiles", activo = true))
        store.insertar(Proveedor(razonSocial = "Distribuidora Industrial Peru E.I.R.L.", ruc = "20587412366",
            celular = "956741258", email = "contacto@dipindustrial.pe", descripcion = "Venta de equipos electronicos", activo = true))
        store.insertar(Proveedor(razonSocial = "Metalurgica Lima Sur SAC", ruc = "20451238761",
            celular = "987123456", email = "ventas@metalurgicalimasur.pe", descripcion = "Estructuras metalicas y planchas", activo = false))
    }

}