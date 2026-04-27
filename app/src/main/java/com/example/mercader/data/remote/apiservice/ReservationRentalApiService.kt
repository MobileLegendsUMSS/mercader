package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.dto.ReservationRequestDto
import com.example.mercader.data.remote.dto.ReservationResponseDto
import com.example.mercader.data.remote.dto.RentalRequestDto
import com.example.mercader.data.remote.dto.RentalResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReservationRentalApiService {

    @POST("reservations/")
    suspend fun createReservation(
        @Body request: ReservationRequestDto
    ): Response<ReservationResponseDto>

    @POST("rentals/")
    suspend fun createRental(
        @Body request: RentalRequestDto
    ): Response<RentalResponseDto>
}
