package com.example.mercader.domain.repositories

import com.example.mercader.domain.models.Game
import com.example.mercader.data.remote.models.*
interface GameRepository {
    suspend fun saveGame(game: Game): Result<Unit>
    suspend fun getGameTypes(): Result<List<Category>>
    suspend fun getDifficulties(): Result<List<Difficulty>>
    suspend fun getEditorials(): Result<List<Editorial>>
    suspend fun deleteGame(id: String, justificacionRetiro: String): Result<Unit>
}