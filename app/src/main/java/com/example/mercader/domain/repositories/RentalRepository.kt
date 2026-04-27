package com.example.mercader.domain.repositories

interface RentalRepository {
    suspend fun createRental(gameId: String, startDate: String, returnDate: String): Result<String>
}
