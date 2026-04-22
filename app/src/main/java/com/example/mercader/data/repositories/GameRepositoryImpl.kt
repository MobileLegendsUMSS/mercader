package com.example.mercader.data.repositories

import com.example.mercader.common.utils.NetworkHandler
import com.example.mercader.data.remote.apiservice.GameApiService
import com.example.mercader.data.remote.models.GameRequestDTO
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.repositories.GameRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

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
                title = game.title,
                description =game.description,
                tutorial = game.tutorial,
                category = game.category,
                nMinPerson = game.nMinPerson,
                nMaxPerson = game.nMaxPerson,
                minMinutes = game.minMinutes,
                maxMinutes = game.maxMinutes,
                difficulty = game.difficulty,
                editorial = game.editorial,
                stock = game.stock,
                price = game.price
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

    override suspend fun getGameTypes(): Result<List<String>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getGameTypes()
            if (response.isSuccessful) {
                val gameCategoryResponse = response.body()
                if (gameCategoryResponse != null && gameCategoryResponse.success) {
                    val gameTypes = gameCategoryResponse.data.map { it.descripcion }
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

    override suspend fun getDifficulties(): Result<List<String>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getDifficulties()
            if (response.isSuccessful) {
                val gameDifficultiesResponse = response.body()
                if (gameDifficultiesResponse != null && gameDifficultiesResponse.success) {
                    val gameDiff = gameDifficultiesResponse.data.map { it.descripcion }
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

    override suspend fun getEditorials(): Result<List<String>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getEditorials()
            if (response.isSuccessful) {
                val gameEditorialResponse = response.body()
                if (gameEditorialResponse != null && gameEditorialResponse.success) {
                    val gameEditorials  = gameEditorialResponse.data.map { it.nombre }
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
}