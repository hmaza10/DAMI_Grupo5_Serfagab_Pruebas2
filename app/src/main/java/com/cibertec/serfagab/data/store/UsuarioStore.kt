package com.cibertec.serfagab.data.store

import android.content.Context
import com.cibertec.serfagab.data.SerfagabContrato
import com.cibertec.serfagab.data.SerfagabDbHelper
import com.cibertec.serfagab.model.Usuario
import kotlin.math.log

class UsuarioStore(context: Context) {

    private val dbHelper = SerfagabDbHelper(context.applicationContext)

    fun validarLogin(login: String, clave: String): Usuario? {
        val columnas = arrayOf(
            SerfagabContrato.COLUMNA_USUARIO_ID,
            SerfagabContrato.COLUMNA_USUARIO_ID_TIPO,
            SerfagabContrato.COLUMNA_USUARIO_NOMBRES,
            SerfagabContrato.COLUMNA_USUARIO_APELLIDOS,
            SerfagabContrato.COLUMNA_USUARIO_EMAIL,
            SerfagabContrato.COLUMNA_USUARIO_LOGIN,
            SerfagabContrato.COLUMNA_USUARIO_CLAVE,
            SerfagabContrato.COLUMNA_USUARIO_ACTIVO
        )

        dbHelper.readableDatabase.query(
            SerfagabContrato.TABLA_USUARIO,
            columnas,
            "${SerfagabContrato.COLUMNA_USUARIO_LOGIN} = ? AND " +
            "${SerfagabContrato.COLUMNA_USUARIO_CLAVE} = ? AND " +
            "${SerfagabContrato.COLUMNA_USUARIO_ACTIVO} = 1",
            arrayOf(login, clave),
            null,
            null,
            null,
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                return Usuario(
                    idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_ID)),
                    idTipo = cursor.getInt(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_ID_TIPO)),
                    nombres = cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_NOMBRES)),
                    apellidos = cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_APELLIDOS)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_EMAIL)),
                    login = cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_LOGIN)),
                    clave = cursor.getString(cursor.getColumnIndexOrThrow(SerfagabContrato.COLUMNA_USUARIO_CLAVE)),
                    activo = true
                )
            }
        }
        return null
    }
}