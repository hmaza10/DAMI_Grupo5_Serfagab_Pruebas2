package com.cibertec.serfagab.model

data class DetalleOrdenCompra(
    val idDetalle: Int = 0,
    val idMaterial: Int,
    val idOrdenCompra: Int,
    val cantidad: Double,
    val precioUnitario: Double,
    val subtotal: Double
)
