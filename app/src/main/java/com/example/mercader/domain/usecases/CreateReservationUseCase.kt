package com.example.mercader.domain.usecases

import com.example.mercader.domain.repositories.ReservationRepository
import javax.inject.Inject

class CreateReservationUseCase @Inject constructor(
    private val reservationRepository: ReservationRepository
) {
    suspend fun execute(gameId: String, date: String, time: String): Result<String> {
        if (gameId.isBlank()) {
            return Result.failure(Exception("Invalid game ID."))
        }
        if (date.isBlank()) {
            return Result.failure(Exception("The reservation date is required."))
        }
        if (time.isBlank()) {
            return Result.failure(Exception("The reservation time is required."))
        }
        return reservationRepository.createReservation(gameId, date, time)
    }
}
