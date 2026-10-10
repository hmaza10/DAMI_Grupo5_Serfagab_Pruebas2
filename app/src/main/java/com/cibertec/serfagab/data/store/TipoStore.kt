package com.cibertec.serfagab.data.store

import android.content.Context
import com.cibertec.serfagab.data.SerfagabContrato
import com.cibertec.serfagab.data.SerfagabDbHelper
import com.cibertec.serfagab.model.Tipo

class TipoStore(context: Context) {

    private val dbHelper = SerfagabDbHelper(context.applicationContext)

    fun obtenerTodos(): List<Tipo> {
        val tipos = mutableListOf<Tipo>()
        val columnas = arrayOf(
            SerfagabContrato.COLUMNA_TIPOU_ID,
            SerfagabContrato.COLUMNA_TIPOU_DESCRIPCION
        )

        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_TIPOU,
            columnas,
            null,
            null,
            null,
            null,
            "${SerfagabContrato.COLUMNA_TIPOU_ID} ASC"
        ).use { cursor ->
            val indiceId = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_TIPOU_ID)
            val indiceDescripcion = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_TIPOU_DESCRIPCION)

            while (cursor.moveToNext()) {
                tipos.add(
                    Tipo(
                        idTipo = cursor.getInt(indiceId),
                        descripcion = cursor.getString(indiceDescripcion) ?: ""
                    )
                )
            }
        }
        return tipos
    }

}