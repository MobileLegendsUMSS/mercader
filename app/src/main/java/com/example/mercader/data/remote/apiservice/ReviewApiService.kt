package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.CreateReviewRequest
import com.example.mercader.data.remote.models.CreateReviewResponse
import com.example.mercader.data.remote.models.GetReviewsResponse
import retrofit2.Response
import retrofit2.http.*

interface ReviewApiService {

    @GET("/api/resenas/juego/{idJuego}")
    suspend fun getReviewsByGame(
        @Path("idJuego") idJuego: String
    ): Response<GetReviewsResponse>

    @POST("/api/resenas")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): Response<CreateReviewResponse>
}