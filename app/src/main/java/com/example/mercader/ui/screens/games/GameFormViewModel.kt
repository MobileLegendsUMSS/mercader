package com.example.mercader.ui.screens.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Difficulty
import com.example.mercader.data.remote.models.Editorial
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameFormViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameFormState())
    val state: StateFlow<GameFormState> = _state.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val gameTypesResult = gameRepository.getGameTypes()
                val gameTypes = if (gameTypesResult.isSuccess) {
                    gameTypesResult.getOrNull() ?: emptyList()
                } else {
                    emptyList()
                }
                val difficultiesResult = gameRepository.getDifficulties()

                val difficulties = if (difficultiesResult.isSuccess) {
                    println("Paso Penultimo${difficultiesResult.getOrNull()}")
                    difficultiesResult.getOrNull() ?: emptyList()
                } else {
                    emptyList()
                }

                val editorialsResult = gameRepository.getEditorials()
                val editorials = if (editorialsResult.isSuccess) {
                    editorialsResult.getOrNull() ?: emptyList()
                } else {
                    emptyList()
                }

                _state.update { currentState ->
                    currentState.copy(
                        gameCategories = gameTypes,
                        difficulties = difficulties,
                        editorials = editorials,
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

    fun updateGameTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun updateGameDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun updateTutorial(tutorial: String) {
        _state.update { it.copy(tutorial = tutorial) }
    }

    fun updateCategory(category: Category) {
        _state.update { it.copy(category = category) }
    }

    fun updateMinPerson(minP: Float) {
        _state.update { it.copy(nMinPerson = minP.toInt()) }
    }

    fun updateMaxPerson(maxP: Float) {
        _state.update { it.copy(nMaxPerson = maxP.toInt()) }
    }

    fun updateMinTime(minT: Float) {
        _state.update { it.copy(minMinutes = minT.toInt()) }
    }

    fun updateMaxTime(maxT: Float) {
        _state.update { it.copy(maxMinutes = maxT.toInt()) }
    }

    fun updateDifficulty(difficulty: Difficulty) {
        _state.update { it.copy(difficulty = difficulty) }
    }

    fun updateEditorial(editorial: Editorial) {
        _state.update { it.copy(editorial = editorial) }
    }

    fun updateStock(stock: String) {
        _state.update { it.copy(stock = stock.toIntOrNull() ?: 0) }
    }

    fun updatePrice(price: String) {
        _state.update { it.copy(price = price.toFloatOrNull() ?: 0f) }
    }

    fun saveGame() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                val currentState = _state.value
                val game = Game(
                    id = System.currentTimeMillis().toString(),
                    title = currentState.title,
                    description = currentState.description,
                    tutorial = currentState.tutorial,
                    category = currentState.category,
                    nMinPerson = currentState.nMinPerson,
                    nMaxPerson = currentState.nMaxPerson,
                    minMinutes = currentState.minMinutes,
                    maxMinutes = currentState.maxMinutes,
                    difficulty = currentState.difficulty,
                    editorial = currentState.editorial,
                    stock = currentState.stock,
                    price = currentState.price,
                )

                val result = gameRepository.saveGame(game)

                if (result.isSuccess) {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            saveSuccess = true,
                            errorMessage = null
                        )
                    }
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Error al guardar el juego: $error"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetSuccess() {
        _state.update { it.copy(saveSuccess = false) }
    }
}