package com.cibertec.serfagab.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cibertec.serfagab.data.db.SerfagabContrato

class SerfagabDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    SerfagabContrato.NOMBRE_BASE_DATOS,
    null,
    SerfagabContrato.VERSION_BASE_DATOS
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SerfagabContrato.SQL_CREAR_TABLA_TIPO_MATERIAL)
        db.execSQL(SerfagabContrato.SQL_CREAR_TABLA_PROVEEDOR)
        db.execSQL(SerfagabContrato.SQL_CREAR_TABLA_MATERIAL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}