package com.example.mercader.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.domain.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadAllCarousels()
    }

    fun loadAllCarousels() {
        loadRecentGames()
        loadMostVisitedGames()
        loadMostSoldGames()
        loadMostBorrowedGames()
    }

    private fun loadRecentGames() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingRecent = true) }
            val result = gameRepository.getRecentGames()
            if (result.isFailure) android.util.Log.e("HomeViewModel", "Error en recientes: ", result.exceptionOrNull())
            _state.update { 
                it.copy(
                    isLoadingRecent = false,
                    recentGames = result.getOrNull() ?: emptyList(),
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun loadMostVisitedGames() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingVisited = true) }
            val result = gameRepository.getMostVisitedGames()
            if (result.isFailure) android.util.Log.e("HomeViewModel", "Error en visitados: ", result.exceptionOrNull())
            _state.update { 
                it.copy(
                    isLoadingVisited = false,
                    mostVisitedGames = result.getOrNull() ?: emptyList(),
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun loadMostSoldGames() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSold = true) }
            val result = gameRepository.getMostSoldGames()
            if (result.isFailure) android.util.Log.e("HomeViewModel", "Error en comprados: ", result.exceptionOrNull())
            _state.update { 
                it.copy(
                    isLoadingSold = false,
                    mostSoldGames = result.getOrNull() ?: emptyList(),
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun loadMostBorrowedGames() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingBorrowed = true) }
            val result = gameRepository.getMostBorrowedGames()
            if (result.isFailure) android.util.Log.e("HomeViewModel", "Error en prestados: ", result.exceptionOrNull())
            _state.update { 
                it.copy(
                    isLoadingBorrowed = false,
                    mostBorrowedGames = result.getOrNull() ?: emptyList(),
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
}
