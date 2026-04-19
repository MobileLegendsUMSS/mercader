package com.example.mercader.data.repositories
// implementacion de la interfaz de game repository segun lo que pide la app
import com.example.mercader.common.constants.*
import com.example.mercader.common.utils.OptionsProvider
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.repositories.GameRepository
import kotlinx.coroutines.delay

class GameRepositoryImpl : GameRepository {

    override suspend fun saveGame(event: Game): Result<Unit> {
        return try {
            delay(500)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getGameTypes(): List<String> =
        OptionsProvider.getGameTypes()

    override suspend fun getDifficulties(): List<String> =
        OptionsProvider.getDifficulties()

    override suspend fun getEditorials(): List<String> =
        OptionsProvider.getEditorials()
}