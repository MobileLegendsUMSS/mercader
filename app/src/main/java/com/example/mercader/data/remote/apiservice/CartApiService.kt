package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.CartRequest
import com.example.mercader.data.remote.models.CartResponse
import com.example.mercader.data.remote.models.DeleteFromCartRequest
import retrofit2.Response
import retrofit2.http.*

interface CartApiService {

    @POST("/api/servicios/usuarios/carrito")
    suspend fun addToCart(
        @Body request: CartRequest
    ): Response<CartResponse>

    @GET("/api/servicios/usuarios/carrito")
    suspend fun getCart(
        @Query("id_usuario") userId: String
    ): Response<CartResponse>

    @PATCH("/api/servicios/usuarios/carrito")
    suspend fun updateQuantity(
        @Body request: CartRequest
    ): Response<CartResponse>

    @HTTP(method = "DELETE", path = "/api/servicios/usuarios/carrito", hasBody = true)
    suspend fun removeFromCart(
        @Body request: DeleteFromCartRequest
    ): Response<CartResponse>
}