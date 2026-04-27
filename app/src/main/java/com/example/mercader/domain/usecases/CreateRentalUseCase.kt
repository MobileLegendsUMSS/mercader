package com.example.mercader.domain.usecases

import com.example.mercader.domain.repositories.RentalRepository
import javax.inject.Inject

class CreateRentalUseCase @Inject constructor(
    private val rentalRepository: RentalRepository
) {
    suspend fun execute(gameId: String, startDate: String, returnDate: String): Result<String> {
        if (gameId.isBlank()) {
            return Result.failure(Exception("Invalid game ID."))
        }
        if (startDate.isBlank()) {
            return Result.failure(Exception("The start date is required."))
        }
        if (returnDate.isBlank()) {
            return Result.failure(Exception("The return date is required."))
        }
        return rentalRepository.createRental(gameId, startDate, returnDate)
    }
}
