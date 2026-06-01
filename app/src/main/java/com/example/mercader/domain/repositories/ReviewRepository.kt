package com.example.mercader.domain.repositories

import com.example.mercader.domain.models.Review

interface ReviewRepository {
    suspend fun getReviewsByGame(gameId: String): Result<List<Review>>
    suspend fun createReview(gameId: String, rating: Int, content: String): Result<Review>
}