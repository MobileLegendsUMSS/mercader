package com.example.mercader.data.repository

import com.example.mercader.data.remote.apiservice.ReviewApiService
import com.example.mercader.data.remote.models.CreateReviewRequest
import com.example.mercader.domain.models.Review
import com.example.mercader.domain.repositories.ReviewRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val apiService: ReviewApiService
) : ReviewRepository {

    override suspend fun getReviewsByGame(gameId: String): Result<List<Review>> {
        return try {
            println("📝 ReviewRepository: Obteniendo reseñas del juego: $gameId")

            val response = apiService.getReviewsByGame(gameId)

            if (response.isSuccessful && response.body()?.success == true) {
                val reviews = response.body()?.data?.map { item ->
                    Review(
                        id = item.idResena,
                        userId = item.usuario.id,
                        userName = item.usuario.nombre,
                        content = item.content,
                        timestamp = item.timestamp
                    )
                } ?: emptyList()

                println("✅ ${reviews.size} reseñas obtenidas")
                Result.success(reviews)
            } else {
                val errorMessage = response.body()?.message ?: "Error al obtener reseñas"
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

    override suspend fun createReview(gameId: String, content: String): Result<Review> {
        return try {
            println("📝 ReviewRepository: Creando reseña para juego: $gameId")
            println("📝 Contenido: ${content.take(50)}...")

            val request = CreateReviewRequest(
                idJuego = gameId,
                content = content
            )

            val response = apiService.createReview(request)

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    val review = Review(
                        id = data.idResena,
                        userId = data.usuario.id,
                        userName = data.usuario.nombre,
                        content = data.content,
                        timestamp = data.timestamp
                    )
                    println("✅ Reseña creada con ID: ${review.id}")
                    Result.success(review)
                } else {
                    Result.failure(Exception("Respuesta sin datos"))
                }
            } else {
                val errorMessage = response.body()?.message ?: "Error al crear reseña"
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