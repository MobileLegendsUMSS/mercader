package com.example.mercader.common.utils

import com.example.mercader.domain.models.Review
import com.example.mercader.domain.repositories.ReviewRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewManager @Inject constructor(
    private val reviewRepository: ReviewRepository
) {

    suspend fun getReviewsByGame(gameId: String): List<Review> {
        return try {
            val result = reviewRepository.getReviewsByGame(gameId)
            if (result.isSuccess) {
                result.getOrNull() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("❌ ReviewManager: Error al obtener reseñas - ${e.message}")
            emptyList()
        }
    }

    suspend fun createReview(gameId: String, rating: Int, content: String): Boolean {
        return try {
            val result = reviewRepository.createReview(gameId, rating, content)
            if (result.isSuccess) {
                println("✅ ReviewManager: Reseña creada exitosamente")
                true
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                println("❌ ReviewManager: Error - $error")
                false
            }
        } catch (e: Exception) {
            println("❌ ReviewManager: Excepción - ${e.message}")
            false
        }
    }
}