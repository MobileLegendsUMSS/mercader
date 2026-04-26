package com.example.mercader.data.remote.models

data class GameResponseDTO(
    val id: String,
    val title: String
)
data class ApiResponseDTO(

    val success: Boolean,
    val data: List<GameResponseDTO>,
    val message: String? = null
)