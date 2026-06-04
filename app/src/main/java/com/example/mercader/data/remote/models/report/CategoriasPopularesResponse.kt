package com.example.mercader.data.remote.models.report

data class CategoriasPopularesResponse(
    val success: Boolean,
    val message: String,
    val data: List<CategoriaPopular>
)

data class CategoriaPopular(
    val descripcion: String,
    val frecuencia: Double // porcentaje
)