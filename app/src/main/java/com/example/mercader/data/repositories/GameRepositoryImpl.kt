package com.example.mercader.data.repositories

import com.example.mercader.common.utils.NetworkHandler
import com.example.mercader.data.remote.apiservice.GameApiService
import com.example.mercader.data.remote.models.GameRequestDTO
import com.example.mercader.data.remote.dto.DeleteGameRequestDto
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.repositories.GameRepository
import com.example.mercader.data.remote.models.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import android.util.Log

class GameRepositoryImpl @Inject constructor(
    private val apiService: GameApiService,
    private val networkHandler: NetworkHandler
) : GameRepository {

    override suspend fun saveGame(game: Game): Result<Unit> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexion a internet"))
            }

            val requestDTO = GameRequestDTO(
                titulo = game.title,
                descripcion =game.description,
                tutorial = game.tutorial,
                category = game.category.id,
                cant_min_pers = game.nMinPerson,
                cant_max_pers = game.nMaxPerson,
                duracion_min = game.minMinutes,
                duracion_max = game.maxMinutes,
                id_dificultad = game.difficulty.id,
                id_editorial = game.editorial.id,
                cantidad = game.stock,
                precio = game.price
            )

            val response = apiService.saveGame(requestDTO)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGameTypes(): Result<List<Category>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getGameTypes()
            if (response.isSuccessful) {
                val gameCategoryResponse = response.body()
                if (gameCategoryResponse != null && gameCategoryResponse.success) {
                    val gameTypes = gameCategoryResponse.data.map { Category(it._id, it.descripcion)  }
                    Result.success(gameTypes)
                } else {
                    Result.failure(Exception("Error en la respuesta del servidor"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDifficulties(): Result<List<Difficulty>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getDifficulties()
            println("Paso Primero${response.body()}")
            if (response.isSuccessful) {
                val gameDifficultiesResponse = response.body()
                println("Paso Medio${gameDifficultiesResponse}")
                if (gameDifficultiesResponse != null && gameDifficultiesResponse.success) {
                    println("Paso Sospechoso${gameDifficultiesResponse}")
                    val gameDiff = gameDifficultiesResponse.data.map { Difficulty(it._id, it.descripcion)  }
                    println("Paso AntePenultimo${gameDiff.size}")
                    Result.success(gameDiff)
                } else {
                    Result.failure(Exception("Error en la respuesta del servidor"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEditorials(): Result<List<Editorial>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getEditorials()
            if (response.isSuccessful) {
                val gameEditorialResponse = response.body()
                if (gameEditorialResponse != null && gameEditorialResponse.success) {
                    val gameEditorials  = gameEditorialResponse.data.map {Editorial(it._id,it.nombre)  }
                    Result.success(gameEditorials)
                } else {
                    Result.failure(Exception("Error en la respuesta del servidor"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteGame(id: String, justificacionRetiro: String): Result<Unit> {
        Log.d("GameRepository", "========== REPOSITORY DELETE ==========")
        Log.d("GameRepository", "ID: $id")
        Log.d("GameRepository", "Justificación: $justificacionRetiro")

        return try {
            val request = DeleteGameRequestDto(justificacionRetiro = justificacionRetiro)
            Log.d("GameRepository", "Request creado: $request")

            Log.d("GameRepository", "Llamando a apiService.deleteGame...")
            val response = apiService.deleteGame(id, request)

            Log.d("GameRepository", "Respuesta de API: $response")
            Result.success(Unit)

        } catch (e: HttpException) {
            Log.e("GameRepository", "❌ Error HTTP: ${e.code()} - ${e.message()}")
            Log.e("GameRepository", "Response body: ${e.response()?.errorBody()?.string()}")
            Result.failure(Exception("Error del servidor: ${e.code()}"))
        } catch (e: IOException) {
            Log.e("GameRepository", "❌ Error de red: ${e.message}")
            Result.failure(Exception("Error de conexión: Verifica tu internet"))
        } catch (e: Exception) {
            Log.e("GameRepository", "❌ Error inesperado: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}