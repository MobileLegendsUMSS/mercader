package com.example.mercader.data.remote.models

data class CreateCategoryDTO(
    val descripcion: String
)

data class CreateEditorialDTO(
    val nombre: String,
    val pais: String
)

data class GenericResponseDTO<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)
