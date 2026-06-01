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
                services = listOf(
                    if (game.isPurchaseAvailable) "compra" else null,
                    if (game.isRentAvailable) "alquiler" else null,
                    if (game.isLoanAvailable) "prestamo" else null
                ).filterNotNull(),
                titulo = game.title,
                descripcion =game.description,
                tutorial = game.tutorial,
                cant_min_pers = game.nMinPerson,
                cant_max_pers = game.nMaxPerson,
                duracion_min = game.minMinutes,
                duracion_max = game.maxMinutes,
                precio = game.price,
                disponible = true,
                activo= true,
                cantidad = game.stock,
                id_dificultad = game.difficulty.id,
                id_editorial = game.editorial.id,
            )

            val response = apiService.saveGame(requestDTO,game.category.id)
            Log.d("GameRepository", "Respuesta de API: $response")
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
            if (response.isSuccessful) {
                val gameDifficultiesResponse = response.body()
                if (gameDifficultiesResponse != null && gameDifficultiesResponse.success) {
                    val gameDiff = gameDifficultiesResponse.data.map { Difficulty(it._id, it.descripcion)  }
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

    override suspend fun getGames(): Result<List<Game>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getGames()
            if (response.isSuccessful) {
                val gamesResponse = response.body()
                if (gamesResponse != null && gamesResponse.success) {
                    val games  = gamesResponse.data.map {
                        Game(
                            it._id,
                            it.titulo,
                            it.descripcion,
                            it.tutorial,
                            Category("", ""),
                            it.cant_min_pers,
                            it.cant_max_pers,
                            it.duracion_min,
                            it.duracion_max,
                            Difficulty(it.id_dificultad._id,it.id_dificultad.descripcion),
                            Editorial(it.id_editorial._id,it.id_editorial.nombre),
                            it.cantidad,
                            it.precio
                            )
                    }
                    Result.success(games)
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

    override suspend fun updateGamePartial(gameId: String, updatedFields: Map<String, Any>): Result<Unit> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión a internet"))
            }

            if (updatedFields.isEmpty()) {
                Log.d("GameRepository", "No hay campos para actualizar")
                return Result.success(Unit)
            }

            Log.d("GameRepository", "Actualizando juego $gameId con ${updatedFields.size} campos")

            val fieldUpdates = convertToFieldUpdates(updatedFields)

            // Hacer una llamada por cada campo a actualizar
            val errors = mutableListOf<String>()
            var successCount = 0

            for ((index, fieldUpdate) in fieldUpdates.withIndex()) {
                try {
                    Log.d("GameRepository", "Actualizando campo ${index + 1}/${fieldUpdates.size}: ${fieldUpdate.fieldName}")

                    val response = apiService.editGame(gameId, fieldUpdate)

                    if (response.isSuccessful) {
                        successCount++
                        Log.d("GameRepository", "✓ Campo ${fieldUpdate.fieldName} actualizado")
                    } else {
                        val errorMsg = "Error al actualizar ${fieldUpdate.fieldName}: ${response.code()}"
                        Log.e("GameRepository", "✗ $errorMsg")
                        errors.add(errorMsg)
                    }

                } catch (e: Exception) {
                    val errorMsg = "Error al actualizar ${fieldUpdate.fieldName}: ${e.message}"
                    Log.e("GameRepository", "✗ $errorMsg")
                    errors.add(errorMsg)
                }
            }

            return if (errors.isEmpty()) {
                Log.d("GameRepository", "✅ Todos los $successCount campos actualizados correctamente")
                Result.success(Unit)
            } else {
                val errorSummary = "Se actualizaron $successCount de ${fieldUpdates.size} campos. Errores: ${errors.joinToString("; ")}"
                Log.e("GameRepository", "❌ $errorSummary")
                Result.failure(Exception(errorSummary))
            }

        } catch (e: HttpException) {
            Log.e("GameRepository", "HTTP Error: ${e.code()}")
            Result.failure(Exception("Error del servidor: ${e.code()}"))
        } catch (e: IOException) {
            Log.e("GameRepository", "Network Error: ${e.message}")
            Result.failure(Exception("Error de conexión: Verifica tu internet"))
        } catch (e: Exception) {
            Log.e("GameRepository", "Error inesperado: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun convertToFieldUpdates(updatedFields: Map<String, Any>): List<GameEditDTO> {
        val fieldUpdates = mutableListOf<GameEditDTO>()

        // Mapeo de nuestros nombres de campo a los nombres que espera el backend
        updatedFields.forEach { (key, value) ->
            when (key) {
                "title" -> {
                    fieldUpdates.add(GameEditDTO("titulo", value))
                }
                "description" -> {
                    fieldUpdates.add(GameEditDTO("descripcion", value))
                }
                "tutorial" -> {
                    fieldUpdates.add(GameEditDTO("tutorial", value))
                }
                "nMinPerson" -> {
                    fieldUpdates.add(GameEditDTO("cant_min_pers", value))
                }
                "nMaxPerson" -> {
                    fieldUpdates.add(GameEditDTO("cant_max_pers", value))
                }
                "minMinutes" -> {
                    fieldUpdates.add(GameEditDTO("duracion_min", value))
                }
                "maxMinutes" -> {
                    fieldUpdates.add(GameEditDTO("duracion_max", value))
                }
                "difficulty" -> {
                    val difficultyMap = value as? Map<*, *>
                    difficultyMap?.get("descripcion")?.let { descripcion ->
                        fieldUpdates.add(GameEditDTO("dificultad", descripcion))
                    }
                }
                "editorial" -> {
                    val editorialMap = value as? Map<*, *>
                    editorialMap?.get("nombre")?.let { nombre ->
                        fieldUpdates.add(GameEditDTO("editorial", nombre))
                    }
                }
                "stock" -> {
                    fieldUpdates.add(GameEditDTO("cantidad", value))
                }
                "price" -> {
                    fieldUpdates.add(GameEditDTO("precio", value))
                }
            }
        }

        return fieldUpdates
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