package com.example.mercader.domain.usecases

import com.example.mercader.domain.repositories.GameRepository
import javax.inject.Inject
import android.util.Log

class DeleteGameUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend fun deleteGame(id: String, justificacionRetiro: String) : Result<Unit> {
        Log.d("DeleteGameUseCase", "UseCase recibió: id=$id, justificacion=$justificacionRetiro")

        if (id.isBlank()) {
            Log.e("DeleteGameUseCase", "ID vacío")
            return Result.failure(Exception("Id de juego de mesa invalido."))
        }

        val result = gameRepository.deleteGame(id, justificacionRetiro)
        Log.d("DeleteGameUseCase", "Resultado del repository: ${result.isSuccess}")
        return result
    }
}