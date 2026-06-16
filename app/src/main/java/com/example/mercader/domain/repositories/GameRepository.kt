package com.example.mercader.domain.repositories

import com.example.mercader.domain.models.Game
import com.example.mercader.data.remote.models.*
interface GameRepository {
    suspend fun saveGame(game: Game): Result<Unit>
    suspend fun getGameTypes(): Result<List<Category>>
    suspend fun getDifficulties(): Result<List<Difficulty>>
    suspend fun getEditorials(): Result<List<Editorial>>

    suspend fun createCategory(descripcion: String): Result<Category>
    suspend fun updateCategory(id: String, descripcion: String): Result<Category>
    suspend fun deleteCategory(id: String): Result<Unit>

    suspend fun createEditorial(nombre: String, pais: String): Result<Editorial>
    suspend fun updateEditorial(id: String, nombre: String, pais: String): Result<Editorial>
    suspend fun deleteEditorial(id: String): Result<Unit>

    suspend fun updateGamePartial(gameId: String, updatedFields: Map<String, Any>): Result<Unit>

    suspend fun getGames(): Result<List<Game>>
    suspend fun getRecentGames(): Result<List<Game>>
    suspend fun getMostVisitedGames(): Result<List<Game>>
    suspend fun getMostSoldGames(): Result<List<Game>>
    suspend fun getMostBorrowedGames(): Result<List<Game>>
    suspend fun deleteGame(id: String, justificacionRetiro: String): Result<Unit>

    suspend fun getGameServices(gameId: String): Result<List<String>>
}