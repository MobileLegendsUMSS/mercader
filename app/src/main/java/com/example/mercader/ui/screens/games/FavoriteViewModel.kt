package com.example.mercader.ui.screens.games

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

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun checkFavorite(gameId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.checkFavorite(gameId).fold(
                onSuccess = { fav ->
                    _isFavorite.value = fav
                    _isLoading.value = false
                },
                onFailure = {
                    _isLoading.value = false
                }
            )
        }
    }

    fun toggleFavorite(gameId: String, onShowMessage: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            if (_isFavorite.value) {
                userRepository.removeFavorite(gameId).fold(
                    onSuccess = {
                        _isFavorite.value = false
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
                        _isFavorite.value = true
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
