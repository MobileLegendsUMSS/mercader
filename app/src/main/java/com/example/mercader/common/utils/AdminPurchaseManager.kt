package com.example.mercader.common.utils

import com.example.mercader.data.remote.models.AdminPurchaseDetailDTO
import com.example.mercader.data.remote.models.AdminPurchaseItemDTO
import com.example.mercader.domain.repositories.AdminPurchaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminPurchaseManager @Inject constructor(
    private val adminPurchaseRepository: AdminPurchaseRepository
) {

    suspend fun getAllPurchases(): List<AdminPurchaseItemDTO> {
        return try {
            val result = adminPurchaseRepository.getAllPurchases()
            if (result.isSuccess) {
                result.getOrNull() ?: emptyList()
            } else {
                println("❌ Error al obtener compras: ${result.exceptionOrNull()?.message}")
                emptyList()
            }
        } catch (e: Exception) {
            println("❌ Excepción en getAllPurchases: ${e.message}")
            emptyList()
        }
    }

    suspend fun getPurchaseById(purchaseId: String): AdminPurchaseDetailDTO? {
        return try {
            val result = adminPurchaseRepository.getPurchaseById(purchaseId)
            if (result.isSuccess) {
                result.getOrNull()
            } else {
                println("❌ Error al obtener detalle: ${result.exceptionOrNull()?.message}")
                null
            }
        } catch (e: Exception) {
            println("❌ Excepción en getPurchaseById: ${e.message}")
            null
        }
    }

    suspend fun updatePurchaseState(purchaseId: String, acceptance: String): Boolean {
        return try {
            val result = adminPurchaseRepository.updatePurchaseState(purchaseId, acceptance)
            if (result.isSuccess) {
                println("✅ Estado de compra actualizado: $acceptance")
                true
            } else {
                println("❌ Error al actualizar: ${result.exceptionOrNull()?.message}")
                false
            }
        } catch (e: Exception) {
            println("❌ Excepción en updatePurchaseState: ${e.message}")
            false
        }
    }
}