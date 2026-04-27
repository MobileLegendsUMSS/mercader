package com.example.mercader.ui.screens.games

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Difficulty
import com.example.mercader.data.remote.models.Editorial
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.repositories.GameRepository
import com.example.mercader.domain.usecases.DeleteGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionState())
    val state: StateFlow<CollectionState> = _state.asStateFlow()

    init {
        loadInitialData()
    }

        private fun loadInitialData() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                try {
                    val gamesResult = gameRepository.getGames()
                    val games = if (gamesResult.isSuccess) {
                        gamesResult.getOrNull() ?: emptyList()
                    } else {
                        emptyList()
                    }
                    _state.update { currentState ->
                        currentState.copy(
                            games = games,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar datos: ${e.message}"
                        )
                    }
                }
            }
        }

    @HiltViewModel
    class DeleteViewModel @Inject constructor(
        private val deleteGameUseCase: DeleteGameUseCase
    ): ViewModel() {
        fun deleteGame(id: String, justificacionRetiro: String) {
            viewModelScope.launch {
                Log.d("TestViewModel", "========== INICIO DELETE ==========")
                Log.d("TestViewModel", "ID del juego: $id")
                Log.d("TestViewModel", "Justificación: $justificacionRetiro")

                val result = deleteGameUseCase.deleteGame(id, justificacionRetiro)

                if (result.isSuccess) {
                    Log.d("TestViewModel", "✅ ÉXITO: Juego eliminado")
                } else {
                    Log.e("TestViewModel", "❌ ERROR: ${result.exceptionOrNull()?.message}")
                    result.exceptionOrNull()?.printStackTrace()
                }
                Log.d("TestViewModel", "========== FIN DELETE ==========")
            }
        }
    }
}
