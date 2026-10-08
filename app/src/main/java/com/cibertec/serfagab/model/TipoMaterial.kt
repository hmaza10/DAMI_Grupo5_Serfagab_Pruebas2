package com.cibertec.serfagab.model

data class TipoMaterial(
    val idTipoMaterial: Int = 0,
    val nombre: String,
    val descripcion: String? = "",
    val activo: Boolean = true
)
