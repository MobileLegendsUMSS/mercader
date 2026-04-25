package com.example.mercader.data.remote.models

data class GameDifficulty(
    val _id: String,
    val descripcion: String,
    val createdAt: String,
    val updatedAt: String
)
data class Difficulty(
    val id: String,
    val descripcion: String
)
data class GameDifficultyResponseDTO<T>(
    val success: Boolean,
    val data: T
)