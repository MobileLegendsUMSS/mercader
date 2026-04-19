package com.example.mercader.ui.screen.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.common.constants.SliderType
import com.example.mercader.common.utils.OptionsProvider
import com.example.mercader.domain.models.Game
import com.example.mercader.data.repositories.GameRepositoryImpl
import com.example.mercader.domain.repositories.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameFormViewModel(
    private val gameRepository: GameRepository = GameRepositoryImpl()
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
                _state.update { currentState ->
                    currentState.copy(
                        gameCategories = gameRepository.getGameTypes(),
                        difficulties = gameRepository.getDifficulties(),
                        editorials = gameRepository.getEditorials(),
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

    fun updateTutorial(tutorial : String){
        _state.update { it.copy(tutorial = tutorial) }

    }

    fun updateCategory(category : String){
        _state.update { it.copy(description = category) }
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

    fun updateDifficulty(difficulty: String) {
        _state.update { it.copy(difficulty = difficulty) }
    }

    fun updateEditorial(editorial: String) {
        _state.update { it.copy(editorial = editorial) }
    }

    fun updateStock(stock: String) {
        _state.update { it.copy(stock = stock.toInt()) }
    }

    fun updatePrice(price: String) {
        _state.update { it.copy(price = price.toFloat()) }
    }

    fun saveEvent() {
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
                    _state.update { it.copy(isSaving = false, saveSuccess = true) }
                } else {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Error al guardar el juego"
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

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun resetSuccess() {
        _state.update { it.copy(saveSuccess = false) }
    }
}