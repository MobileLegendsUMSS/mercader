package com.example.mercader.data.repository

import com.example.mercader.data.remote.apiservice.AdminPurchaseApiService
import com.example.mercader.data.remote.models.AdminPurchaseDetailDTO
import com.example.mercader.data.remote.models.AdminPurchaseItemDTO
import com.example.mercader.data.remote.models.UpdatePurchaseStateRequest
import com.example.mercader.domain.repositories.AdminPurchaseRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AdminPurchaseRepositoryImpl @Inject constructor(
    private val apiService: AdminPurchaseApiService
) : AdminPurchaseRepository {

    override suspend fun getAllPurchases(): Result<List<AdminPurchaseItemDTO>> {
        return try {
            println("📋 AdminPurchase: Obteniendo todas las compras")

            val response = apiService.getAllPurchases()

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: emptyList()
                println("✅ ${data.size} compras obtenidas")
                Result.success(data)
            } else {
                val errorMessage = response.body()?.message ?: "Error al obtener compras"
                println("❌ Error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            println("❌ Error de red: ${e.message}")
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            println("❌ Error HTTP: ${e.message}")
            Result.failure(Exception("Error del servidor: ${e.message}"))
        } catch (e: Exception) {
            println("❌ Error inesperado: ${e.message}")
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getPurchaseById(purchaseId: String): Result<AdminPurchaseDetailDTO> {
        return try {
            println("📋 AdminPurchase: Obteniendo compra ID: $purchaseId")

            val response = apiService.getPurchaseById(purchaseId)

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    println("✅ Detalle de compra obtenido")
                    Result.success(data)
                } else {
                    Result.failure(Exception("No se encontraron datos de la compra"))
                }
            } else {
                val errorMessage = response.body()?.message ?: "Error al obtener detalle de compra"
                println("❌ Error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            println("❌ Error de red: ${e.message}")
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            println("❌ Error HTTP: ${e.message}")
            Result.failure(Exception("Error del servidor: ${e.message}"))
        } catch (e: Exception) {
            println("❌ Error inesperado: ${e.message}")
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun updatePurchaseState(purchaseId: String, acceptance: String): Result<Unit> {
        return try {
            println("📋 AdminPurchase: Actualizando compra $purchaseId a estado: $acceptance")

            val request = UpdatePurchaseStateRequest(
                idCompra = purchaseId,
                acceptance = acceptance
            )
            val response = apiService.updatePurchaseState(request)

            if (response.isSuccessful && response.body()?.success == true) {
                println("✅ Estado de compra actualizado correctamente")
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al actualizar estado de compra"
                println("❌ Error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            println("❌ Error de red: ${e.message}")
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            println("❌ Error HTTP: ${e.message}")
            Result.failure(Exception("Error del servidor: ${e.message}"))
        } catch (e: Exception) {
            println("❌ Error inesperado: ${e.message}")
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}