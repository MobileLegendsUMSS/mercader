package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.AdminPurchaseDetailResponseDTO
import com.example.mercader.data.remote.models.AdminPurchaseListResponseDTO
import com.example.mercader.data.remote.models.UpdatePurchaseStateRequest
import com.example.mercader.data.remote.models.UpdatePurchaseStateResponse
import retrofit2.Response
import retrofit2.http.*

interface AdminPurchaseApiService {

    // Obtener todas las compras (admin)
    @GET("/api/servicios/admin/compras")
    suspend fun getAllPurchases(): Response<AdminPurchaseListResponseDTO>

    // Obtener detalle de una compra específica
    @GET("/api/servicios/admin/compra")
    suspend fun getPurchaseById(
        @Query("id_compra") purchaseId: String
    ): Response<AdminPurchaseDetailResponseDTO>

    // Aceptar o rechazar una compra
    @PATCH("/api/servicios/admin/aceptar-compra")
    suspend fun updatePurchaseState(
        @Body request: UpdatePurchaseStateRequest
    ): Response<UpdatePurchaseStateResponse>
}