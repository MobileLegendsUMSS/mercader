package com.example.mercader.ui.screens.games

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
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
    private val gameRepository: GameRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(GameFormState())
    val state: StateFlow<GameFormState> = _state.asStateFlow()
    private var existingGame: Game? = null

    // Guardar el estado original para comparar cambios
    private var originalState: GameFormState? = null

    private val isEditMode: Boolean
        get() = _state.value.id.isNotEmpty()

    private val _isPurchaseAvailable = mutableStateOf(false)
    val isPurchaseAvailable: Boolean get() = _isPurchaseAvailable.value

    private val _isRentAvailable = mutableStateOf(false)
    val isRentAvailable: Boolean get() = _isRentAvailable.value

    private val _isLoanAvailable = mutableStateOf(false)
    val isLoanAvailable: Boolean get() = _isLoanAvailable.value

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

    fun setGameToEdit(game: Game) {
        existingGame = game
        Log.d("Category"," ${existingGame?.category}")
        val newState = GameFormState(
            id = game.id,
            title = game.title,
            description = game.description,
            tutorial = game.tutorial ?: "",
            category = game.category,
            nMinPerson = game.nMinPerson,
            nMaxPerson = game.nMaxPerson,
            minMinutes = game.minMinutes,
            maxMinutes = game.maxMinutes,
            difficulty = game.difficulty,
            editorial = game.editorial,
            stock = game.stock,
            price = game.price,
            gameCategories = _state.value.gameCategories,
            difficulties = _state.value.difficulties,
            editorials = _state.value.editorials
        )

        // Guardar el estado original para comparar cambios
        originalState = newState.copy()
        _state.update { newState }
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

    fun updatePurchaseAvailable(checked: Boolean) {
        _state.update { it.copy(isPurchaseAvailable = checked) }
    }

    fun updateRentAvailable(checked: Boolean) {
        _state.update { it.copy(isRentAvailable = checked) }
    }

    fun updateLoanAvailable(checked: Boolean) {
        _state.update { it.copy(isLoanAvailable = checked) }
    }
    fun saveGame() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val currentState = _state.value

                val result = if (isEditMode) {
                    val updatedFields = getUpdatedFields()
                    if (updatedFields.isNotEmpty()) {
                        gameRepository.updateGamePartial(currentState.id, updatedFields)
                    } else {
                        Result.success(Unit)
                    }
                } else {
                    val game = createFullGame(currentState)
                    gameRepository.saveGame(game)
                }

                result.fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                saveSuccess = true,
                                errorMessage = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isSaving = false,
                                errorMessage = "Error al guardar: ${error.message}"
                            )
                        }
                    }
                )
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

    private fun getUpdatedFields(): Map<String, Any> {
        val currentState = _state.value
        val original = originalState ?: return emptyMap()

        val updatedFields = mutableMapOf<String, Any>()

        if (currentState.title != original.title) {
            updatedFields["title"] = currentState.title
        }

        if (currentState.description != original.description) {
            updatedFields["description"] = currentState.description
        }

        if (currentState.tutorial != original.tutorial) {
            updatedFields["tutorial"] = currentState.tutorial
        }

        if (currentState.category != original.category) {
            updatedFields["difficulty"] = mapOf(
                "id" to currentState.category.id,
                "descripcion" to currentState.category.descripcion
            )
        }

        if (currentState.nMinPerson != original.nMinPerson) {
            updatedFields["nMinPerson"] = currentState.nMinPerson
        }

        if (currentState.nMaxPerson != original.nMaxPerson) {
            updatedFields["nMaxPerson"] = currentState.nMaxPerson
        }

        if (currentState.minMinutes != original.minMinutes) {
            updatedFields["minMinutes"] = currentState.minMinutes
        }

        if (currentState.maxMinutes != original.maxMinutes) {
            updatedFields["maxMinutes"] = currentState.maxMinutes
        }

        if (currentState.difficulty != original.difficulty) {
            updatedFields["difficulty"] = mapOf(
                "id" to currentState.difficulty.id,
                "descripcion" to currentState.difficulty.descripcion
            )
        }

        if (currentState.editorial != original.editorial) {
            updatedFields["editorial"] = mapOf(
                "id" to currentState.editorial.id,
                "nombre" to currentState.editorial.nombre
            )
        }

        if (currentState.stock != original.stock) {
            updatedFields["stock"] = currentState.stock
        }

        if (currentState.price != original.price) {
            updatedFields["price"] = currentState.price
        }

        return updatedFields
    }

    private fun createFullGame(state: GameFormState): Game {
        return Game(
            id = if (state.id.isNotEmpty()) state.id else System.currentTimeMillis().toString(),
            title = state.title,
            description = state.description,
            tutorial = state.tutorial,
            category = state.category,
            nMinPerson = state.nMinPerson,
            nMaxPerson = state.nMaxPerson,
            minMinutes = state.minMinutes,
            maxMinutes = state.maxMinutes,
            difficulty = state.difficulty,
            editorial = state.editorial,
            stock = state.stock,
            price = state.price,
            isPurchaseAvailable = state.isPurchaseAvailable,
            isRentAvailable = state.isRentAvailable,
            isLoanAvailable = state.isLoanAvailable
        )
    }

    fun resetSuccess() {
        _state.update { it.copy(saveSuccess = false) }
    }

    fun resetForm() {
        _state.update {
            GameFormState(
                gameCategories = it.gameCategories,
                difficulties = it.difficulties,
                editorials = it.editorials
            )
        }
        existingGame = null
        originalState = null
    }
    fun clearGameToEdit() {
        existingGame = null
        originalState = null
        resetForm() // Llama al resetForm que ya creamos
    }
}