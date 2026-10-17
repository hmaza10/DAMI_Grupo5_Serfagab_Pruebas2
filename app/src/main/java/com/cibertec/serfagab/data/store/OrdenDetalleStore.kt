package com.cibertec.serfagab.data.store

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.cibertec.serfagab.data.SerfagabContrato
import com.cibertec.serfagab.data.SerfagabDbHelper
import com.cibertec.serfagab.model.DetalleOrdenCompra
import com.cibertec.serfagab.model.OrdenCompra

class OrdenDetalleStore(context: Context){

    private val appContext = context.applicationContext
    private val dbHelper = SerfagabDbHelper(context.applicationContext)

    fun insertarVarias(
        db: SQLiteDatabase,
        idOrdenCompra: Int,
        detalles: List<DetalleOrdenCompra>
    ){
        detalles.forEach { detalle ->
            val valores = ContentValues().apply {

                put(
                    SerfagabContrato.COLUMNA_DETALLE_ORDEN_ID_MATERIAL,
                    detalle.material.idMaterial
                )

                put(
                    SerfagabContrato.COLUMNA_DETALLE_ORDEN_ID_ORDEN,
                    idOrdenCompra
                )

                put(
                    SerfagabContrato.COLUMNA_DETALLE_ORDEN_CANTIDAD,
                    detalle.cantidad
                )

                put(
                    SerfagabContrato.COLUMNA_DETALLE_ORDEN_PRECIO,
                    detalle.precioUnitario
                )

                put(
                    SerfagabContrato.COLUMNA_DETALLE_ORDEN_SUBTOTAL,
                    detalle.subtotal
                )

            }

            val resultado = db.insert(
                SerfagabContrato.TABLA_DETALLE_ORDEN,
                null,
                valores
            )

            check(resultado != -1L) {
                "No se pudo insertar un detalle de la orden"
            }
        }
    }


    fun borrarDe(
        db: SQLiteDatabase,
        idOrdenCompra: Int
    ): Int {
        return db.delete(
            SerfagabContrato.TABLA_DETALLE_ORDEN,
            "${SerfagabContrato.COLUMNA_DETALLE_ORDEN_ID_ORDEN} = ?",
            arrayOf(idOrdenCompra.toString())
        )
    }

    fun obtenerDe(orden: OrdenCompra):
            List<DetalleOrdenCompra> {
        val detalles = mutableListOf<DetalleOrdenCompra>()
        val materialesPorId = MaterialStore(appContext).obtenerTodos().associateBy { it.idMaterial }

        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_DETALLE_ORDEN,
            null,
            "${SerfagabContrato.COLUMNA_DETALLE_ORDEN_ID_ORDEN} = ?",
            arrayOf(orden.idOrdenCompra.toString()),
            null,
            null,
            "${SerfagabContrato.COLUMNA_DETALLE_ORDEN_ID} ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val idMaterial = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_DETALLE_ORDEN_ID_MATERIAL)
                )

                val material = materialesPorId[idMaterial] ?: continue

                detalles.add(
                    DetalleOrdenCompra(
                        idDetalle = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                SerfagabContrato.COLUMNA_DETALLE_ORDEN_ID
                            )
                        ),

                        material = material,
                        ordenCompra = orden,
                        cantidad = cursor.getDouble(
                            cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_DETALLE_ORDEN_CANTIDAD)
                        ),
                        precioUnitario = cursor.getDouble(
                            cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_DETALLE_ORDEN_PRECIO)
                        ),
                        subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_DETALLE_ORDEN_SUBTOTAL))
                    )
                )
            }
        }

        return detalles

    }


}