package com.cibertec.serfagab.data.store

import android.content.ContentValues
import android.content.Context
import com.cibertec.serfagab.data.SerfagabContrato
import com.cibertec.serfagab.data.SerfagabDbHelper
import com.cibertec.serfagab.model.TipoMaterial

class TipoMaterialStore(context: Context) {

    private val dbHelper = SerfagabDbHelper(context.applicationContext)

    fun obtenerTodos(): List<TipoMaterial> {
        val tipos = mutableListOf<TipoMaterial>()
        val columnas = arrayOf(
            SerfagabContrato.COLUMNA_TIPO_ID,
            SerfagabContrato.COLUMNA_TIPO_NOMBRE,
            SerfagabContrato.COLUMNA_TIPO_DESCRIPCION,
            SerfagabContrato.COLUMNA_TIPO_ACTIVO
        )

        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_TIPO_MATERIAL,
            columnas,
            null,
            null,
            null,
            null,
            "${SerfagabContrato.COLUMNA_TIPO_ID} DESC"
        ).use { cursor ->
            val indiceId = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_TIPO_ID)
            val indiceNombre = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_TIPO_NOMBRE)
            val indiceDescripcion = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_TIPO_DESCRIPCION)
            val indiceActivo = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_TIPO_ACTIVO)

            while (cursor.moveToNext()) {
                tipos.add(
                    TipoMaterial(
                        idTipoMaterial = cursor.getInt(indiceId),
                        nombre = cursor.getString(indiceNombre),
                        descripcion = cursor.getString(indiceDescripcion) ?: "",
                        activo = cursor.getInt(indiceActivo) == 1
                    )
                )
            }
        }
        return tipos
    }

    fun insertar(tipo: TipoMaterial): Long {
        val valores = ContentValues().apply {
            if (tipo.idTipoMaterial != 0) put(SerfagabContrato.COLUMNA_TIPO_ID, tipo.idTipoMaterial)
            put(SerfagabContrato.COLUMNA_TIPO_NOMBRE, tipo.nombre)
            put(SerfagabContrato.COLUMNA_TIPO_DESCRIPCION, tipo.descripcion)
            put(SerfagabContrato.COLUMNA_TIPO_ACTIVO, if (tipo.activo) 1 else 0)
        }

        return dbHelper.writableDatabase.insert(
            SerfagabContrato.TABLA_TIPO_MATERIAL,
            null,
            valores
        )
    }

    fun reemplazarTodos(tipos: List<TipoMaterial>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(SerfagabContrato.TABLA_TIPO_MATERIAL,null, null)
            tipos.forEach { insertar(it) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

    }
}
