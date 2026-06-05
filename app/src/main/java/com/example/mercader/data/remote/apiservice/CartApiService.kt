package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.BuyRequest
import com.example.mercader.data.remote.models.BuyResponse
import com.example.mercader.data.remote.models.CartRequest
import com.example.mercader.data.remote.models.CartResponse
import com.example.mercader.data.remote.models.DeleteFromCartRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CartApiService {

    @POST("/api/servicios/usuarios/carrito")
    suspend fun addToCart(
        @Body request: CartRequest
    ): Response<CartResponse>

    @GET("/api/servicios/usuarios/carrito")
    suspend fun getCart(): Response<CartResponse>

    @PATCH("/api/servicios/usuarios/carrito")
    suspend fun updateQuantity(
        @Body request: CartRequest
    ): Response<CartResponse>

    @HTTP(method = "DELETE", path = "/api/servicios/usuarios/carrito", hasBody = true)
    suspend fun removeFromCart(
        @Body request: DeleteFromCartRequest
    ): Response<CartResponse>

    @POST("/api/servicios/usuarios/compra")
    suspend fun checkout(
        @Body request: BuyRequest
    ): Response<BuyResponse>

    @Multipart
    @POST("/api/servicios/usuarios/compra")
    suspend fun checkoutWithReceipt(
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part comprobante: MultipartBody.Part
    ): Response<BuyResponse>
}