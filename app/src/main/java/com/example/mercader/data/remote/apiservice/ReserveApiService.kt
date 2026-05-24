package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.ReserveFilterRequest
import com.example.mercader.data.remote.models.ReserveRequest
import com.example.mercader.data.remote.models.ReserveResponse
import retrofit2.Response
import retrofit2.http.*

interface ReserveApiService {

    // Registrar prestamo (POST con query params + body)
    @POST("/api/servicios/usuarios/prestamo")
    suspend fun registerReserve(
        @Query("id_usuario") userId: String,
        @Query("id_juego") gameId: String,
        @Body request: ReserveRequest
    ): Response<ReserveResponse>

    // Ver prestamos del usuario (GET con query + body)
    @GET("/api/servicios/usuarios/prestamos")
    suspend fun getReserves(
        @Query("id_usuario") userId: String,
        @Body filters: ReserveFilterRequest
    ): Response<ReserveResponse>
}