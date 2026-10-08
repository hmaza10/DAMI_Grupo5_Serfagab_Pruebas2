package com.cibertec.serfagab.model


data class OrdenCompra(
    val idOrdenCompra: Int = 0,
    val idProveedor: Int,
    val idUsuario: Int,
    val observaciones: String,
    val fecha: String,
    val total: Double,
    val estado: String
)
