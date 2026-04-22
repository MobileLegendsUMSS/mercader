package com.example.mercader.data.remote.models

data class GameEditorial(
    val id: String,
    val pais: String,
    val nombre: String,
    val createdAt: String,
    val updatedAt: String
)
data class GameEditorialResponseDTO<T>(
    val success: Boolean,
    val data: T
)