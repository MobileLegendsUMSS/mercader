package com.example.mercader.ui.screens.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Difficulty
import com.example.mercader.data.remote.models.Editorial
import com.example.mercader.domain.repositories.GameRepository
import com.example.mercader.common.utils.GameFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FilterState())
    val state: StateFlow<FilterState> = _state.asStateFlow()

    // Flow para que el GameScreen escuche los filtros aplicados
    private val _filtersApplied = MutableStateFlow<GameFilters?>(null)
    val filtersApplied: StateFlow<GameFilters?> = _filtersApplied.asStateFlow()

    init {
        loadFilterOptions()
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Usar el mismo repositorio que GameFormViewModel
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

    fun updateCategory(category: Category?) {
        _state.update {
            it.copy(selectedCategory = category)
        }
        updateHasActiveFilters()
    }

    fun updateMinPerson(value: Float) {
        val intValue = value.toInt()
        _state.update { currentState ->
            if (intValue <= currentState.nMaxPerson) {
                currentState.copy(nMinPerson = intValue)
            } else {
                currentState.copy(
                    nMinPerson = currentState.nMaxPerson,
                    nMaxPerson = intValue
                )
            }
        }
        updateHasActiveFilters()
    }

    fun updateMaxPerson(value: Float) {
        val intValue = value.toInt()
        _state.update { currentState ->
            if (intValue >= currentState.nMinPerson) {
                currentState.copy(nMaxPerson = intValue)
            } else {
                currentState.copy(
                    nMaxPerson = currentState.nMinPerson,
                    nMinPerson = intValue
                )
            }
        }
        updateHasActiveFilters()
    }

    fun updateMinTime(value: Float) {
        val intValue = value.toInt()
        _state.update { currentState ->
            if (intValue <= currentState.maxMinutes) {
                currentState.copy(minMinutes = intValue)
            } else {
                currentState.copy(
                    minMinutes = currentState.maxMinutes,
                    maxMinutes = intValue
                )
            }
        }
        updateHasActiveFilters()
    }

    fun updateMaxTime(value: Float) {
        val intValue = value.toInt()
        _state.update { currentState ->
            if (intValue >= currentState.minMinutes) {
                currentState.copy(maxMinutes = intValue)
            } else {
                currentState.copy(
                    maxMinutes = currentState.minMinutes,
                    minMinutes = intValue
                )
            }
        }
        updateHasActiveFilters()
    }

    fun updateDifficulty(difficulty: Difficulty?) {
        _state.update { it.copy(selectedDifficulty = difficulty) }
        updateHasActiveFilters()
    }

    fun updateEditorial(editorial: Editorial?) {
        _state.update { it.copy(selectedEditorial = editorial) }
        updateHasActiveFilters()
    }

    fun updateMinPrice(value: String) {
        val doubleValue = value.toDoubleOrNull()
        _state.update { currentState ->
            if (doubleValue == null) {
                currentState.copy(minPrice = null)
            } else if (currentState.maxPrice == null || doubleValue <= currentState.maxPrice!!) {
                currentState.copy(minPrice = doubleValue)
            } else {
                currentState.copy(
                    minPrice = currentState.maxPrice,
                    maxPrice = doubleValue
                )
            }
        }
        updateHasActiveFilters()
    }

    fun updateMaxPrice(value: String) {
        val doubleValue = value.toDoubleOrNull()
        _state.update { currentState ->
            if (doubleValue == null) {
                currentState.copy(maxPrice = null)
            } else if (currentState.minPrice == null || doubleValue >= currentState.minPrice!!) {
                currentState.copy(maxPrice = doubleValue)
            } else {
                currentState.copy(
                    maxPrice = currentState.minPrice,
                    minPrice = doubleValue
                )
            }
        }
        updateHasActiveFilters()
    }

    fun clearAllFilters() {
        _state.update { currentState ->
            currentState.copy(
                selectedCategory = null,
                nMinPerson = 1,
                nMaxPerson = 8,
                minMinutes = 0,
                maxMinutes = 240,
                selectedDifficulty = null,
                selectedEditorial = null,
                minPrice = null,
                maxPrice = null,
                hasActiveFilters = false,
                errorMessage = null
            )
        }
    }

    fun applyFilters() {
        viewModelScope.launch {
            _filtersApplied.emit(getActiveFilters())
        }
    }

    private fun updateHasActiveFilters() {
        val currentState = _state.value
        val hasActive = currentState.selectedCategory != null ||
                currentState.nMinPerson != 1 ||
                currentState.nMaxPerson != 8 ||
                currentState.minMinutes != 0 ||
                currentState.maxMinutes != 240 ||
                currentState.selectedDifficulty != null ||
                currentState.selectedEditorial != null ||
                currentState.minPrice != null ||
                currentState.maxPrice != null

        _state.update { it.copy(hasActiveFilters = hasActive) }
    }

    fun getActiveFilters(): GameFilters {
        val currentState = _state.value
        return GameFilters(
            category = currentState.selectedCategory,
            minPlayers = currentState.nMinPerson,
            maxPlayers = currentState.nMaxPerson,
            minDuration = currentState.minMinutes,
            maxDuration = currentState.maxMinutes,
            difficulty = currentState.selectedDifficulty,
            editorial = currentState.selectedEditorial,
            minPrice = currentState.minPrice,
            maxPrice = currentState.maxPrice
        )
    }

    fun resetFiltersApplied() {
        _filtersApplied.update { null }
    }
}
