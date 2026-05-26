package com.example.mercader.data.repositories

import android.content.ContentValues.TAG
import android.util.Log
import com.example.mercader.data.remote.apiservice.UserApiService
import com.example.mercader.domain.models.UserProfile
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService
) : UserRepository {

    override suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val response = userApiService.getUserProfile()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.result && body.data != null) {
                    val dto = body.data
                    Result.success(
                        UserProfile(
                            id = userId,
                            username = dto.username ?: "",
                            name = dto.name ?: "",
                            lastName = dto.lastName ?: "",
                            phone = dto.phone ?: "",
                            email = dto.email ?: "",
                            mercaPoints = dto.mercaPoints ?: 0
                        )
                    )
                } else {
                    Result.failure(Exception(body?.message ?: "Respuesta vacía del servidor"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkFavorite(gameId: String): Result<Boolean> {
        return try {
            val result = getFavorites()
            if (result.isSuccess) {
                val favorites = result.getOrNull() ?: emptyList()
                Log.d(TAG, "checkFavorite: Favorites count: ${favorites.size}")  // LOG 4: Número de favoritos
                Log.d(TAG, "checkFavorite: Favorite IDs: ${favorites.map { it.id }}")  // LOG 5: IDs de favoritos
                Log.d(TAG, "checkFavorite: Looking for gameId: $gameId")  // LOG 6: ID buscado

                val isFavorite = favorites.any { it.id == gameId }
                Log.d(TAG, "checkFavorite: Result - isFavorite: $isFavorite")  // LOG 7: Resultado final

                Result.success(isFavorite)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Error al obtener favoritos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addFavorite(gameId: String): Result<Unit> {
        return try {
            val response = userApiService.addFavorite(gameId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al agregar a favoritos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFavorite(gameId: String): Result<Unit> {
        return try {
            val response = userApiService.removeFavorite(gameId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al eliminar de favoritos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFavorites(): Result<List<Game>> {
        return try {
            val response = userApiService.getFavorites()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val games = body.data?.map {
                        Game(
                            id = it.id_juego,
                            title = it.titulo,
                            description = it.descripcion,
                            tutorial = "",
                            category = com.example.mercader.data.remote.models.Category("", ""),
                            nMinPerson = 0,
                            nMaxPerson = 0,
                            minMinutes = 0,
                            maxMinutes = 0,
                            difficulty = com.example.mercader.data.remote.models.Difficulty("", ""),
                            editorial = com.example.mercader.data.remote.models.Editorial("", ""),
                            stock = 0,
                            price = it.precio
                        )
                    } ?: emptyList()
                    Result.success(games)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al obtener favoritos"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

