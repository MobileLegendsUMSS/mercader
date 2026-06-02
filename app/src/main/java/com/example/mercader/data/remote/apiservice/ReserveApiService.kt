package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.ReserveFilterRequest
import com.example.mercader.data.remote.models.ReserveRequest
import com.example.mercader.data.remote.models.ReserveResponse
import retrofit2.Response
import retrofit2.http.*

interface ReserveApiService {

    @POST("/api/servicios/usuarios/prestamo")
    suspend fun registerReserve(
        @Query("id_juego") gameId: String,
        @Body request: ReserveRequest
    ): Response<ReserveResponse>

    @GET("/api/servicios/usuarios/prestamos")
    suspend fun getReserves(
        @Query("vigente") vigente: Boolean,
        @Query("recogido") recogido: Boolean,
        @Query("devuelto") devuelto: Boolean
    ): Response<ReserveResponse>
}