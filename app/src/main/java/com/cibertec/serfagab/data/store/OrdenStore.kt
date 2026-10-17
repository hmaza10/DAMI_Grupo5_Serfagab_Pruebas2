package com.cibertec.serfagab.data.store

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.cibertec.serfagab.data.SerfagabContrato
import com.cibertec.serfagab.data.SerfagabDbHelper
import com.cibertec.serfagab.model.OrdenCompra
import com.cibertec.serfagab.model.Tipo
import com.cibertec.serfagab.model.Usuario


class OrdenStore(context: Context) {

    private val dbHelper = SerfagabDbHelper(context.applicationContext)
    private val proveedorStore = ProveedorStore(context.applicationContext)
    private val detalleStore = OrdenDetalleStore(context.applicationContext)

    fun insertarOrden(orden: OrdenCompra): Long {
        val db = dbHelper.writableDatabase
        var idOrden = -1L

        db.beginTransaction()
        try {
            val valores = ContentValues().apply {
                if (orden.idOrdenCompra != 0) {
                    put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID, orden.idOrdenCompra)

                }
                    put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID_PROVEEDOR, orden.proveedor.idProveedor)
                    put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID_USUARIO, orden.usuario.idUsuario)
                    put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_OBSERVACIONES, orden.observaciones)
                    put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_FECHA, orden.fecha)
                    put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_TOTAL, orden.total)
                    put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ESTADO, orden.estado)
                    put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_PENDIENTE_SYNC, 1)
                }

                idOrden = db.insert(SerfagabContrato.TABLA_ORDEN_COMPRA, null, valores)

                if (idOrden == -1L) {
                    throw kotlin.IllegalStateException("No se pudo insertar la orden")

                }
                detalleStore.insertarVarias(
                    db,
                    idOrden.toInt(),
                    orden.detalles
                )
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }

            return idOrden
        }

    fun insertarVarias(ordenes: List<OrdenCompra>): List<Long> {
        val ids = mutableListOf<Long>()
        ordenes.forEach { orden ->
            ids.add(insertarOrden(orden))
        }
        return ids
    }

    fun listarPendientes(): List<OrdenCompra> {
        val ordenes = mutableListOf<OrdenCompra>()

        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_ORDEN_COMPRA,
            null,
            "${SerfagabContrato.COLUMNA_ORDEN_COMPRA_PENDIENTE_SYNC} = ?",
            arrayOf("1"),
            null,
            null,
            "${SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID} DESC"
        ).use { cursor ->
            while (cursor.moveToNext()){
                val id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID)
                )
                obtenerPorId(id)?.let { ordenes.add(it) }

            }
        }

        return ordenes
    }

    fun obtenerPorId(idOrdenCompra: Int): OrdenCompra? {
        var resultado: OrdenCompra? = null

        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_ORDEN_COMPRA,
            null,
            "${SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID} = ?",
            arrayOf(idOrdenCompra.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                val idProveedor = cursor.getInt(

                    cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID_PROVEEDOR)

                )

                val idUsuario = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID_USUARIO)

                )

                val proveedor = proveedorStore.obtenerTodos()
                    .firstOrNull { it.idProveedor == idProveedor}
                    ?: return null

                val usuario = obtenerUsuario(idUsuario) ?: return null

                val ordenBase = OrdenCompra(
                    idOrdenCompra = idOrdenCompra,
                    proveedor = proveedor,
                    usuario = usuario,
                    observaciones = cursor.getString(

                        cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_ORDEN_COMPRA_OBSERVACIONES)
                    ),
                    fecha = cursor.getString(

                        cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_ORDEN_COMPRA_FECHA)
                    ),
                    total = cursor.getDouble(

                        cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_ORDEN_COMPRA_TOTAL)
                    ),
                    estado = cursor.getString(

                        cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ESTADO)
                    ),

                    detalles = emptyList()
                )

                resultado = ordenBase.copy(
                    detalles = detalleStore.obtenerDe(ordenBase)
                )
            }
        }

        return resultado
    }

    fun actualizar(orden: OrdenCompra): Int {
        val db = dbHelper.writableDatabase
        var filas = 0

        db.beginTransaction()
        try {
            val valores = ContentValues().apply {
                put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID_PROVEEDOR, orden.proveedor.idProveedor)
                put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID_USUARIO, orden.usuario.idUsuario)
                put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_OBSERVACIONES, orden.observaciones)
                put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_FECHA, orden.fecha)
                put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_TOTAL, orden.total)
                put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_ESTADO, orden.estado)
                put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_PENDIENTE_SYNC, 1)
            }

            filas = db.update(
                SerfagabContrato.TABLA_ORDEN_COMPRA,
                valores,
                "${SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID} = ?",
                arrayOf(orden.idOrdenCompra.toString())
            )

            if (filas > 0) {
                detalleStore.borrarDe(db, orden.idOrdenCompra)
                detalleStore.insertarVarias(
                    db,
                    orden.idOrdenCompra,
                    orden.detalles
                )
                db.setTransactionSuccessful()
            }
        }finally {
            db.endTransaction()
        }
        return filas
    }

    fun borrar(idOrdenCompra: Int): Int {
        val db = dbHelper.writableDatabase
        var filas = 0

        db.beginTransaction()
        try {
            detalleStore.borrarDe(db, idOrdenCompra)

            filas = db.delete(
                SerfagabContrato.TABLA_ORDEN_COMPRA,
                "${SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID} = ?",
                arrayOf(idOrdenCompra.toString())
            )

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        return filas
    }

    fun marcarSincronizada(idOrdenCompra: Int): Int {
        val valores = ContentValues().apply {
            put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_PENDIENTE_SYNC, 0)
        }

        return dbHelper.writableDatabase.update(
            SerfagabContrato.TABLA_ORDEN_COMPRA,
            valores,
            "${SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID} = ?",
            arrayOf(idOrdenCompra.toString())
        )
    }

    fun marcarFallida(idOrdenCompra: Int): Int {
        val valores = ContentValues().apply {
            put(SerfagabContrato.COLUMNA_ORDEN_COMPRA_PENDIENTE_SYNC, 1)
        }

        return dbHelper.writableDatabase.update(
            SerfagabContrato.TABLA_ORDEN_COMPRA,
            valores,
            "${SerfagabContrato.COLUMNA_ORDEN_COMPRA_ID} = ?",
            arrayOf(idOrdenCompra.toString())
        )
    }

    private fun obtenerUsuario(idUsuario: Int): Usuario? {
        var usuario: Usuario? = null
        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_USUARIO,
            null,
            "${SerfagabContrato.COLUMNA_USUARIO_ID} = ?",
            arrayOf(idUsuario.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToNext()) {
                val idTipo = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_ID_TIPO)
                )
                val tipo = obtenerTipo(idTipo) ?: Tipo(idTipo, "")

                usuario = Usuario(
                    idUsuario = idUsuario,
                    tipo = tipo,
                    nombres =
                        cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_NOMBRES)),
                    apellidos = cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_APELLIDOS)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_EMAIL)),
                    login = cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_LOGIN)),
                    clave = cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_CLAVE)),
                    activo = cursor.getInt(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_ACTIVO)) == 1,

                    )
            }
        }
        return usuario
    }

    private fun obtenerTipo(idTipo: Int): Tipo? {
        var tipo: Tipo? = null

        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_TIPOU,
            null,
            "${SerfagabContrato.COLUMNA_TIPOU_ID} = ?",
            arrayOf(idTipo.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToNext()) {
                tipo = Tipo(
                    idTipo = idTipo,
                    descripcion = cursor.getString(

                        cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_TIPOU_DESCRIPCION)

                    )
                )
            }
        }

        return tipo
    }








}
