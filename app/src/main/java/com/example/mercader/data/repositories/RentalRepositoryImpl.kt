package com.example.mercader.data.repositories

import com.example.mercader.common.utils.NetworkHandler
import com.example.mercader.data.remote.apiservice.ReservationRentalApiService
import com.example.mercader.data.remote.dto.RentalRequestDto
import com.example.mercader.domain.repositories.RentalRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RentalRepositoryImpl @Inject constructor(
    private val apiService: ReservationRentalApiService,
    private val networkHandler: NetworkHandler
) : RentalRepository {

    override suspend fun createRental(gameId: String, startDate: String, returnDate: String): Result<String> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No internet connection"))
            }

            val request = RentalRequestDto(
                game_id = gameId,
                start_date = startDate,
                return_date = returnDate
            )

            val response = apiService.createRental(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.message)
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to create rental"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
