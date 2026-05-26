package com.example.mercader.domain.repositories

import com.example.mercader.domain.models.UserProfile
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.models.UserPurchase
import com.example.mercader.domain.models.UserLoan

interface UserRepository {
    suspend fun getUserProfile(userId: String): Result<UserProfile>
    suspend fun checkFavorite(gameId: String): Result<Boolean>
    suspend fun addFavorite(gameId: String): Result<Unit>
    suspend fun removeFavorite(gameId: String): Result<Unit>
    suspend fun getFavorites(): Result<List<Game>>
    suspend fun getUserPurchases(): Result<List<UserPurchase>>
    suspend fun getUserLoans(): Result<List<UserLoan>>
}

