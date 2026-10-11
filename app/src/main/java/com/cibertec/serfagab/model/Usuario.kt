package com.cibertec.serfagab.model

data class Usuario(
    val idUsuario: Int = 0,
    val tipo: Tipo,
    val nombres: String,
    val apellidos: String,
    val email: String,
    val login: String,
    val clave: String? = null,
    val activo: Boolean = true
)
