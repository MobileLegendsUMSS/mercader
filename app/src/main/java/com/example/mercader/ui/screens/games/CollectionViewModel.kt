package com.example.mercader.ui.screens.games

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.common.utils.GameFilter
import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Difficulty
import com.example.mercader.data.remote.models.Editorial
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.repositories.GameRepository
import com.example.mercader.domain.usecases.DeleteGameUseCase
import com.example.mercader.common.utils.GameFilters
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

    // Almacenar todos los juegos sin filtrar
    private var allGames: List<Game> = emptyList()

    // Almacenar los filtros actuales
    private var currentFilters: GameFilters? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val gamesResult = gameRepository.getGames()
                val size = gamesResult.toString()
                Log.d("GameRec", "ID del juego: $size")
                val games = if (gamesResult.isSuccess) {
                    gamesResult.getOrNull() ?: emptyList()
                } else {
                    emptyList()
                }

                // Guardar todos los juegos
                allGames = games

                // Aplicar filtros si existen
                applyCurrentFilters()

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = null
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

    private fun applyCurrentFilters() {
        val filteredGames = GameFilter.applyFilters(allGames, currentFilters)
        _state.update {
            it.copy(
                games = filteredGames,
                activeFilters = currentFilters,
                hasActiveFilters = GameFilter.hasActiveFilters(currentFilters),
                filtersSummary = GameFilter.getFiltersSummary(currentFilters)
            )
        }

    }

    /**
     * Actualiza los filtros y refresca la lista
     * @param filters Nuevos filtros a aplicar
     */
    fun updateFilters(filters: GameFilters?) {
        currentFilters = filters
        applyCurrentFilters()
    }

    /**
     * Limpia todos los filtros y muestra todos los juegos
     */
    fun clearFilters() {
        currentFilters = null
        applyCurrentFilters()
    }

    /**
     * Recarga los juegos desde el repositorio y aplica filtros
     * @param filters Filtros opcionales a aplicar después de recargar
     */
    fun reloadGamesWithFilters(filters: GameFilters? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val gamesResult = gameRepository.getGames()
                val games = if (gamesResult.isSuccess) {
                    gamesResult.getOrNull() ?: emptyList()
                } else {
                    emptyList()
                }

                // Actualizar todos los juegos
                allGames = games

                // Actualizar filtros si se proporcionaron
                if (filters != null) {
                    currentFilters = filters
                }

                // Aplicar filtros
                applyCurrentFilters()

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al recargar datos: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Refresca los datos (sin cambiar filtros)
     */
    fun refreshGames() {
        reloadGamesWithFilters(currentFilters)
    }

    // Mantener tu DeleteViewModel interno si lo necesitas
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