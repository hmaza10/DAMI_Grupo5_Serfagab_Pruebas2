package com.cibertec.serfagab.model

data class Usuario(
    val idUsuario: Int = 0,
    val idTipo: Int,
    val nombres: String,
    val apellidos: String,
    val email: String,
    val login: String,
    val clave: String,
    val activo: Boolean = true
)
