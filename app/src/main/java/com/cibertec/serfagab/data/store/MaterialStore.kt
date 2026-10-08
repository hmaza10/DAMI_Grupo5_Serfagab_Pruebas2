package com.cibertec.serfagab.data.store

import android.content.ContentValues
import android.content.Context
import com.cibertec.serfagab.data.SerfagabContrato
import com.cibertec.serfagab.data.SerfagabDbHelper
import com.cibertec.serfagab.model.Material

class MaterialStore(context: Context) {

    private val dbHelper = SerfagabDbHelper(context.applicationContext)

    fun agregar(material: Material): Long {
        val valores = ContentValues().apply {
            if (material.idMaterial != 0) put(SerfagabContrato.COLUMNA_MATERIAL_ID, material.idMaterial)
            put(SerfagabContrato.COLUMNA_MATERIAL_ID_TIPO, material.idTipoMaterial)
            put(SerfagabContrato.COLUMNA_MATERIAL_NOMBRE, material.nombre)
            put(SerfagabContrato.COLUMNA_MATERIAL_UNIDAD, material.unidadMedida)
            put(SerfagabContrato.COLUMNA_MATERIAL_STOCK, material.stockActual)
            put(SerfagabContrato.COLUMNA_MATERIAL_PRECIO, material.precioReferencial)
            put(SerfagabContrato.COLUMNA_MATERIAL_DESCRIPCION, material.descripcion)
            put(SerfagabContrato.COLUMNA_MATERIAL_VERSION, material.version)
            put(SerfagabContrato.COLUMNA_MATERIAL_ACTIVO, if (material.activo) 1 else 0)
        }

        return dbHelper.writableDatabase.insert(
            SerfagabContrato.TABLA_MATERIAL,
            null,
            valores
        )
    }

    fun obtenerTodos(): List<Material> {
        val materiales = mutableListOf<Material>()
        val columnas = arrayOf(
            SerfagabContrato.COLUMNA_MATERIAL_ID,
            SerfagabContrato.COLUMNA_MATERIAL_ID_TIPO,
            SerfagabContrato.COLUMNA_MATERIAL_NOMBRE,
            SerfagabContrato.COLUMNA_MATERIAL_UNIDAD,
            SerfagabContrato.COLUMNA_MATERIAL_STOCK,
            SerfagabContrato.COLUMNA_MATERIAL_PRECIO,
            SerfagabContrato.COLUMNA_MATERIAL_DESCRIPCION,
            SerfagabContrato.COLUMNA_MATERIAL_VERSION,
            SerfagabContrato.COLUMNA_MATERIAL_ACTIVO
        )

        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_MATERIAL,
            columnas,
            null,
            null,
            null,
            null,
            "${SerfagabContrato.COLUMNA_MATERIAL_ID} DESC"
        ).use { cursor ->
            val indiceId = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_MATERIAL_ID)
            val indiceIdTipo = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_MATERIAL_ID_TIPO)
            val indiceNombre = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_MATERIAL_NOMBRE)
            val indiceUnidad = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_MATERIAL_UNIDAD)
            val indiceStock = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_MATERIAL_STOCK)
            val indicePrecio = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_MATERIAL_PRECIO)
            val indiceDescripcion = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_MATERIAL_DESCRIPCION)
            val indiceVersion = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_MATERIAL_VERSION)
            val indiceActivo = cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_MATERIAL_ACTIVO)

            while (cursor.moveToNext()) {
                materiales.add(
                    Material(
                        idMaterial = cursor.getInt(indiceId),
                        idTipoMaterial = cursor.getInt(indiceIdTipo),
                        nombre = cursor.getString(indiceNombre),
                        unidadMedida = cursor.getString(indiceUnidad)?: "",
                        stockActual = cursor.getDouble(indiceStock),
                        precioReferencial = cursor.getDouble(indicePrecio),
                        descripcion = cursor.getString(indiceDescripcion),
                        version = cursor.getInt(indiceVersion),
                        activo = cursor.getInt(indiceActivo) == 1
                    )
                )
            }
        }
        return materiales
    }

    fun actualizar(material: Material): Int {
        val valores = ContentValues().apply {
            put(SerfagabContrato.COLUMNA_MATERIAL_ID_TIPO, material.idTipoMaterial)
            put(SerfagabContrato.COLUMNA_MATERIAL_NOMBRE, material.nombre)
            put(SerfagabContrato.COLUMNA_MATERIAL_UNIDAD, material.unidadMedida)
            put(SerfagabContrato.COLUMNA_MATERIAL_STOCK, material.stockActual)
            put(SerfagabContrato.COLUMNA_MATERIAL_PRECIO, material.precioReferencial)
            put(SerfagabContrato.COLUMNA_MATERIAL_DESCRIPCION, material.descripcion)
            put(SerfagabContrato.COLUMNA_MATERIAL_VERSION, material.version)
            put(SerfagabContrato.COLUMNA_MATERIAL_ACTIVO, if (material.activo) 1 else 0)
        }

        return dbHelper.writableDatabase.update(
            SerfagabContrato.TABLA_MATERIAL,
            valores,
            "${SerfagabContrato.COLUMNA_MATERIAL_ID} = ?",
            arrayOf(material.idMaterial.toString())
        )
    }

    fun desactivar(idMaterial: Int): Int {
        val valores = ContentValues().apply {
            put(SerfagabContrato.COLUMNA_MATERIAL_ACTIVO, 0)
        }
        return dbHelper.writableDatabase.update(
            SerfagabContrato.TABLA_MATERIAL,
            valores,
            "${SerfagabContrato.COLUMNA_MATERIAL_ID} = ?",
            arrayOf(idMaterial.toString())
        )
    }

    fun activar(idMaterial: Int): Int {
        val valores = ContentValues().apply {
            put(SerfagabContrato.COLUMNA_MATERIAL_ACTIVO, 1)
        }
        return dbHelper.writableDatabase.update(
            SerfagabContrato.TABLA_MATERIAL,
            valores,
            "${SerfagabContrato.COLUMNA_MATERIAL_ID} = ?",
            arrayOf(idMaterial.toString())
        )
    }

}