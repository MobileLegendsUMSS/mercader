package com.example.mercader.ui.screens.profile

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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = userRepository.getUserProfile(userId)
                result.fold(
                    onSuccess = { profile ->
                        _state.value = _state.value.copy(
                            username = profile.username,
                            name = profile.name,
                            lastName = profile.lastName,
                            phone = profile.phone,
                            email = profile.email,
                            mercaPoints = profile.mercaPoints,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar el perfil"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar el perfil"
                )
            }
            loadFavorites()
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isFavoritesLoading = true, favoritesError = null)
            try {
                val result = userRepository.getFavorites()
                result.fold(
                    onSuccess = { favorites ->
                        _state.value = _state.value.copy(
                            favorites = favorites,
                            isFavoritesLoading = false,
                            favoritesError = null
                        )
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(
                            isFavoritesLoading = false,
                            favoritesError = error.message ?: "Error al cargar favoritos"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isFavoritesLoading = false,
                    favoritesError = e.message ?: "Error al cargar favoritos"
                )
            }
        }
    }
}

