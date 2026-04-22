package com.example.mercader.data.remote.models

data class GameDifficulty(
    val id: String,
    val descripcion: String,
    val createdAt: String,
    val updatedAt: String
)
data class GameDifficultyResponseDTO<T>(
    val success: Boolean,
    val data: T
)