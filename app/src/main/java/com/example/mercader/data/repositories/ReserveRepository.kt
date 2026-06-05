package com.example.mercader.domain.repositories

interface ReserveRepository {
    suspend fun registerReserve(
        gameId: String,
        serviceType: String,
        reserveDate: String
    ): Result<Unit>
}