package com.example.mercader.domain.repositories

interface ReserveRepository {
    suspend fun registerReserve(
        userId: String,
        gameId: String,
        serviceType: String,
        reserveDate: String
    ): Result<Unit>
}