package com.example.mercader.domain.repositories

interface ReservationRepository {
    suspend fun createReservation(gameId: String, date: String, time: String): Result<String>
}
