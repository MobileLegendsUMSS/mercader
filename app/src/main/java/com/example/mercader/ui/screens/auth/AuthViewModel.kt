package com.example.mercader.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.data.remote.AuthApi
import com.example.mercader.data.remote.LoginRequest
import com.example.mercader.domain.usecases.AuthenticationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val isAdmin: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val authUseCase: AuthenticationUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(nombre: String, contrasenna: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // 1. Hacer llamada HTTP a /api/login
                val response = authApi.login(
                    LoginRequest(nombre = nombre, contrasenna = contrasenna)
                )

                // 2. GUARDAR TOKEN EN CACHE ← AQUÍ ES DONDE SE GUARDA AUTOMÁTICAMENTE
                authUseCase.saveAuthToken(response.token)

                // 3. Resetear el contador de 15 días
                authUseCase.refreshAccessTime()

                // 4. Determinar si es admin por el sufijo
                val isAdmin = contrasenna.endsWith("#adm")

                // 5. Navegar al home
                _authState.value = AuthState.Success(isAdmin)

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Error en la autenticación"
                )
            }
        }
    }

    fun signin(nombre: String, contrasenna: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // 1. Hacer llamada HTTP a /api/signin
                val response = authApi.signin(
                    LoginRequest(nombre = nombre, contrasenna = contrasenna)
                )

                // 2. GUARDAR TOKEN EN CACHE ← AQUÍ ES DONDE SE GUARDA AUTOMÁTICAMENTE
                authUseCase.saveAuthToken(response.token)

                // 3. Resetear el contador de 15 días
                authUseCase.refreshAccessTime()

                // 4. Determinar si es admin por el sufijo
                val isAdmin = contrasenna.endsWith("#adm")

                // 5. Navegar al home
                _authState.value = AuthState.Success(isAdmin)

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Error en el registro"
                )
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
    fun checkAuthentication(
        onAuthenticated: (isAdmin: Boolean) -> Unit,
        onNotAuthenticated: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val isAuthenticated = authUseCase.isUserAuthenticated()
                if (isAuthenticated) {
                    authUseCase.refreshAccessTime()
                    onAuthenticated(false) // TODO: obtener isAdmin del token
                } else {
                    onNotAuthenticated()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onNotAuthenticated()
            }
        }
    }
    suspend fun getAuthToken(): String? = authUseCase.getAuthToken()
    suspend fun isUserAuthenticated(): Boolean = authUseCase.isUserAuthenticated()
}
