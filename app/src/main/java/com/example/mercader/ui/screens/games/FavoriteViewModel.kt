package com.example.mercader.ui.screens.games

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    fun isFavorite(gameId: String): Boolean = _favoriteIds.value.contains(gameId)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = userRepository.getFavorites()
                result.fold(
                    onSuccess = { games ->
                        games.forEach { Log.d("FAVORITE_DEBUG", "Favorite ID: '${it.id}'") }
                        _favoriteIds.value = games.map { it.id }.toSet()
                        _isLoading.value = false
                        _error.value = null
                    },
                    onFailure = { error ->
                        _isLoading.value = false
                        _error.value = error.message ?: "Error al cargar favoritos"
                    }
                )
            } catch (e: Exception) {  // ← el try/catch que le faltaba
                _isLoading.value = false
                _error.value = e.message ?: "Error al cargar favoritos"
            }
        }
    }

    fun toggleFavorite(gameId: String, onShowMessage: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val isCurrentlyFavorite = _favoriteIds.value.contains(gameId)

            if (isCurrentlyFavorite) {
                userRepository.removeFavorite(gameId).fold(
                    onSuccess = {
                        _favoriteIds.value = _favoriteIds.value - gameId
                        _isLoading.value = false
                        onShowMessage("Juego eliminado de favoritos")
                    },
                    onFailure = { error ->
                        _isLoading.value = false
                        onShowMessage(error.message ?: "Error al eliminar de favoritos")
                    }
                )
            } else {
                userRepository.addFavorite(gameId).fold(
                    onSuccess = {
                        _favoriteIds.value = _favoriteIds.value + gameId
                        _isLoading.value = false
                        onShowMessage("Juego agregado a favoritos")
                    },
                    onFailure = { error ->
                        _isLoading.value = false
                        onShowMessage(error.message ?: "Error al agregar a favoritos")
                    }
                )
            }
        }
    }
}
