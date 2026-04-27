package com.example.mercader.data.remote.models

data class GameResponseDTO(
    val _id: String,
    val titulo: String,
    val descripcion: String,
    val tutorial: String,
    val cant_min_pers: Int,
    val cant_max_pers: Int,
    val duracion_min: Int,
    val duracion_max: Int,
    val precio: Int,
    val disponible: Boolean,
    val cantidad: Int,
    val id_dificultad: GameDifficulty,
    val id_editorial: GameEditorial,
    val createdAt: String,
    val updatedAt: String,
    val activo: Boolean,
    val justificacionRetiro: String?
)

data class AllGamesResponseDTO<T>(
    val success: Boolean,
    val data: T
)

