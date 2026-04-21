package com.example.mercader.data.remote.models

data class GameCategory(
    val id: String,
    val descripcion: String,
    val createdAt: String,
    val updatedAt: String
)
data class GameCategoryResponseDTO<T>(
    val success: Boolean,
    val data: T
)