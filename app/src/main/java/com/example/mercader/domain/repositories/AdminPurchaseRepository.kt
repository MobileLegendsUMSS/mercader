package com.example.mercader.domain.repositories

import com.example.mercader.data.remote.models.AdminPurchaseDetailDTO
import com.example.mercader.data.remote.models.AdminPurchaseItemDTO

interface AdminPurchaseRepository {
    suspend fun getAllPurchases(): Result<List<AdminPurchaseItemDTO>>
    suspend fun getPurchaseById(purchaseId: String): Result<AdminPurchaseDetailDTO>
    suspend fun updatePurchaseState(purchaseId: String, acceptance: String): Result<Unit>
}