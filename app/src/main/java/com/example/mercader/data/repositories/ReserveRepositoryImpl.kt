package com.example.mercader.data.repository

import com.example.mercader.data.remote.apiservice.ReserveApiService
import com.example.mercader.data.remote.models.ReserveRequest
import com.example.mercader.domain.repositories.ReserveRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ReserveRepositoryImpl @Inject constructor(
    private val apiService: ReserveApiService
) : ReserveRepository {

    override suspend fun registerReserve(
        userId: String,
        gameId: String,
        tipoServicio: String,
        fechaPrestamo: String
    ): Result<Unit> {
        return try {
            //println("📋 PrestamoRepository: Registrando préstamo")
            //println("📋 userId: $userId, gameId: $gameId")
            //println("📋 tipoServicio: $tipoServicio, fecha: $fechaPrestamo")

            val request = ReserveRequest(
                servicio = tipoServicio,
                fechaPrestamo = fechaPrestamo
            )

            val response = apiService.registerReserve(userId, gameId, request)

            println("📋 Response code: ${response.code()}")
            println("📋 Response successful: ${response.isSuccessful}")
            println("📋 Response body: ${response.body()}")

            if (response.isSuccessful && response.body()?.success == true) {
                println("✅ Préstamo registrado exitosamente")
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al registrar préstamo"
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
            e.printStackTrace()
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}