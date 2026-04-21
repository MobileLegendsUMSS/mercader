package com.example.mercader.domain.repositories

import com.example.mercader.domain.models.Game

interface GameRepository {
    suspend fun saveGame(game: Game): Result<Unit>
    suspend fun getGameTypes(): Result<List<String>>
    suspend fun getDifficulties(): Result<List<String>>
    suspend fun getEditorials(): Result<List<String>>
}

