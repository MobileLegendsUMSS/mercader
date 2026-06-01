package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

// Request para crear reseña
data class CreateReviewRequest(
    @SerializedName("id_juego")
    val idJuego: String,
    val content: String
)

// Respuesta al crear reseña
data class CreateReviewResponse(
    val success: Boolean,
    val message: String,
    val data: ReviewData? = null
)

// Datos de la reseña (respuesta)
data class ReviewData(
    @SerializedName("id_resena")
    val idResena: String,
    val content: String,
    val timestamp: Long,
    val usuario: UsuarioReviewData
)

// Usuario dentro de la reseña
data class UsuarioReviewData(
    val id: String,
    val nombre: String
)

// Respuesta al obtener reseñas
data class GetReviewsResponse(
    val success: Boolean,
    val message: String,
    val data: List<ReviewItemResponse>? = null
)

// Item de reseña en la lista
data class ReviewItemResponse(
    @SerializedName("id_resena")
    val idResena: String,
    val content: String,
    val timestamp: Long,
    val usuario: UsuarioReviewData
)