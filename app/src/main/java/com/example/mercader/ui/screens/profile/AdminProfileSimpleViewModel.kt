package com.example.mercader.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.domain.repositories.UserRepository
import com.example.mercader.domain.usecases.AuthenticationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminProfileSimpleState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val username: String = "",
    val name: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val rol: String = ""
)

@HiltViewModel
class AdminProfileSimpleViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authUseCase: AuthenticationUseCase  // ← Inyectar AuthenticationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminProfileSimpleState())
    val state: StateFlow<AdminProfileSimpleState> = _state.asStateFlow()

    init {
        loadAdminProfile()
        loadUserRol()
    }

    private fun loadAdminProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                // No necesitamos userId porque el backend lo obtiene del token
                val result = userRepository.getUserProfile("")
                result.fold(
                    onSuccess = { profile ->
                        _state.value = _state.value.copy(
                            username = profile.username,
                            name = profile.name,
                            lastName = profile.lastName,
                            phone = profile.phone,
                            email = profile.email,
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
        }
    }

    private fun loadUserRol() {
        viewModelScope.launch {
            val rol = authUseCase.getUserRol()
            _state.value = _state.value.copy(
                rol = when(rol?.lowercase()) {
                    "superadmin" -> "SUPER ADMIN"
                    "admin" -> "ADMINISTRADOR"
                    else -> "ADMIN"
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
        }
    }
}