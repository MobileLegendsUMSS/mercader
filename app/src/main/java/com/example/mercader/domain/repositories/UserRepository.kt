package com.example.mercader.domain.repositories

import com.example.mercader.domain.models.UserProfile
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.models.UserPurchase
import com.example.mercader.domain.models.UserLoan
import com.example.mercader.domain.models.TopGame

interface UserRepository {
    suspend fun getUserProfile(userId: String): Result<UserProfile>
    suspend fun checkFavorite(gameId: String): Result<Boolean>
    suspend fun addFavorite(gameId: String): Result<Unit>
    suspend fun removeFavorite(gameId: String): Result<Unit>
    suspend fun getFavorites(): Result<List<Game>>
    suspend fun getUserPurchases(): Result<List<UserPurchase>>
    suspend fun getUserLoans(): Result<List<UserLoan>>
    suspend fun getTopGames(): Result<List<TopGame>>

    suspend fun editProfile(updatedFields: Map<String, Any>): Result<Unit>

    // Obtener todos los prestamos (admin)
    suspend fun getAllLoans(
        vigente: Boolean,
        recogido: Boolean,
        devuelto: Boolean
    ): Result<List<UserLoan>>

    // Actualizar prestamo (admin)
    suspend fun updateLoan(
        loanId: String,
        fechaInicio: String?,
        fechaFin: String?
    ): Result<Unit>
}

