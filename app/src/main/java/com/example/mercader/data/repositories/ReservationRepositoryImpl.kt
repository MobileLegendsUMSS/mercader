package com.example.mercader.data.repositories

import com.example.mercader.common.utils.NetworkHandler
import com.example.mercader.data.remote.apiservice.ReservationRentalApiService
import com.example.mercader.data.remote.dto.ReservationRequestDto
import com.example.mercader.domain.repositories.ReservationRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val apiService: ReservationRentalApiService,
    private val networkHandler: NetworkHandler
) : ReservationRepository {

    override suspend fun createReservation(gameId: String, date: String, time: String): Result<String> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No internet connection"))
            }

            val request = ReservationRequestDto(
                game_id = gameId,
                date = date,
                time = time
            )

            val response = apiService.createReservation(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.message)
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to create reservation"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
