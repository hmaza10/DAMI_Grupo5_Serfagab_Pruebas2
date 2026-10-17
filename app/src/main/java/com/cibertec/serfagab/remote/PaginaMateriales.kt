package com.cibertec.serfagab.remote

import com.cibertec.serfagab.model.Material

data class PaginaMateriales(
    val content: List<Material>,
    val last: Boolean
)
