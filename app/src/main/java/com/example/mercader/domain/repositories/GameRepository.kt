package com.example.mercader.domain.repositories

import com.example.mercader.domain.models.Game

interface GameRepository {
    suspend fun saveGame(event: Game): Result<Unit>
    suspend fun getGameTypes(): List<String>
    suspend fun getDifficulties(): List<String>
    suspend fun getEditorials(): List<String>
}

