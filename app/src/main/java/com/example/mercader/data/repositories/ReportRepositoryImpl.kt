package com.example.mercader.data.repositories


import android.util.Log
import com.example.mercader.data.remote.apiservice.ReportApiService
import com.example.mercader.data.remote.models.report.*
import com.example.mercader.data.repository.ReportRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val apiService: ReportApiService
) : ReportRepository {

    override suspend fun getJuegosStock(allGames: Boolean, order: String, amount: Int?): Result<ReportStockResponse> {
        return try {
            val request = ReportStockRequest(allGames, order, amount)
            Log.d("JuegosStock", "${request}")
            val response = apiService.getJuegosStock(request)
            Log.d("JuegosStock", "${response}")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener juegos por stock: ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getCategoriasPopulares(): Result<CategoriasPopularesResponse> {
        return try {
            val response = apiService.getCategoriasPopulares()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener categorías populares: ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getIngresosPeriodo(timePeriod: String, timeValue: String): Result<IngresosPeriodoResponse> {
        return try {
            val request = ReportPeriodoRequest(timePeriod, timeValue)
            val response = apiService.getIngresosPeriodo(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener ingresos: ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getUsosPeriodo(timePeriod: String, timeValue: String): Result<UsosPeriodoResponse> {
        return try {
            val request = ReportPeriodoRequest(timePeriod, timeValue)
            val response = apiService.getUsosPeriodo(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener usos: ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}