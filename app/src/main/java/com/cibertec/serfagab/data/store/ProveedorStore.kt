package com.cibertec.serfagab.data.store

import android.content.ContentValues
import android.content.Context
import com.cibertec.serfagab.data.SerfagabContrato
import com.cibertec.serfagab.data.SerfagabDbHelper
import com.cibertec.serfagab.model.Proveedor

class ProveedorStore(context: Context) {

    private val dbHelper = SerfagabDbHelper(context.applicationContext)

    fun obtenerTodos(): List<Proveedor> {
        val proveedores = mutableListOf<Proveedor>()
        val columnas = arrayOf(
            SerfagabContrato.COLUMNA_PROVEEDOR_ID,
            SerfagabContrato.COLUMNA_PROVEEDOR_RAZON_SOCIAL,
            SerfagabContrato.COLUMNA_PROVEEDOR_RUC,
            SerfagabContrato.COLUMNA_PROVEEDOR_CELULAR,
            SerfagabContrato.COLUMNA_PROVEEDOR_EMAIL,
            SerfagabContrato.COLUMNA_PROVEEDOR_DESCRIPCION,
            SerfagabContrato.COLUMNA_PROVEEDOR_ACTIVO
        )

        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_PROVEEDOR,
            columnas,
            null,
            null,
            null,
            null,
            "${SerfagabContrato.COLUMNA_PROVEEDOR_ID} DESC"
        ).use { cursor ->
            val indiceId = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_PROVEEDOR_ID)
            val indiceRazonSocial = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_PROVEEDOR_RAZON_SOCIAL)
            val indiceRuc = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_PROVEEDOR_RUC)
            val indiceCelular = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_PROVEEDOR_CELULAR)
            val indiceEmail = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_PROVEEDOR_EMAIL)
            val indiceDescripcion = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_PROVEEDOR_DESCRIPCION)
            val indiceActivo = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_PROVEEDOR_ACTIVO)

            while (cursor.moveToNext()) {
                proveedores.add(
                    Proveedor(
                        idProveedor = cursor.getInt(indiceId),
                        razonSocial = cursor.getString(indiceRazonSocial),
                        ruc = cursor.getString(indiceRuc),
                        celular = cursor.getString(indiceCelular),
                        email = cursor.getString(indiceEmail),
                        descripcion = cursor.getString(indiceDescripcion),
                        activo = cursor.getInt(indiceActivo) == 1
                    )
                )
            }
        }
        return proveedores
    }

    fun insertar(proveedor: Proveedor): Long {
        val valores = ContentValues().apply {
            if (proveedor.idProveedor != 0) put(SerfagabContrato.COLUMNA_PROVEEDOR_ID, proveedor.idProveedor)
            put(SerfagabContrato.COLUMNA_PROVEEDOR_RAZON_SOCIAL, proveedor.razonSocial)
            put(SerfagabContrato.COLUMNA_PROVEEDOR_RUC, proveedor.ruc)
            put(SerfagabContrato.COLUMNA_PROVEEDOR_CELULAR, proveedor.celular)
            put(SerfagabContrato.COLUMNA_PROVEEDOR_EMAIL, proveedor.email)
            put(SerfagabContrato.COLUMNA_PROVEEDOR_DESCRIPCION, proveedor.descripcion)
            put(SerfagabContrato.COLUMNA_PROVEEDOR_ACTIVO, if (proveedor.activo) 1 else 0)
        }

        return dbHelper.writableDatabase.insert(
            SerfagabContrato.TABLA_PROVEEDOR,
            null,
            valores
        )
    }

    fun reemplazarTodos(proveedor: List<Proveedor>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(SerfagabContrato.TABLA_PROVEEDOR, null,null)
            proveedor.forEach { insertar(it) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

    }

}