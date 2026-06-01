package com.example.mercader.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.data.remote.apiservice.AuthApiService
import com.example.mercader.data.remote.apiservice.LoginRequest
import com.example.mercader.data.remote.apiservice.SigninRequest
import com.example.mercader.domain.usecases.AuthenticationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val isAdmin: Boolean, val rol: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authApiService: AuthApiService,
    private val authUseCase: AuthenticationUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(nombre: String, contrasenna: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val response = authApiService.login(
                    LoginRequest(nombre = nombre, contrasenna = contrasenna)
                )

                if (response.isSuccessful && response.body() != null) {
                    val authBody = response.body()!!

                    // Guardar token y rol
                    authUseCase.saveAuthToken(authBody.token, authBody.rol)
                    authUseCase.refreshAccessTime()

                    // Determinar si es admin basado en el rol del backend
                    val isAdmin = authUseCase.isAdmin(authBody.rol)

                    _authState.value = AuthState.Success(isAdmin, authBody.rol)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Usuario o contraseña incorrectos"
                    _authState.value = AuthState.Error(errorMsg)
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Error en la autenticación"
                )
            }
        }
    }

    fun signin(
        nombre: String,
        contrasenna: String,
        nombres: String?,
        apellidos: String?,
        telefono: String,
        correo_contacto: String
    ) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val response = authApiService.signin(
                    SigninRequest(
                        nombre = nombre,
                        contrasenna = contrasenna,
                        nombres = nombres,
                        apellidos = apellidos,
                        telefono = telefono,
                        correo_contacto = correo_contacto
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val authBody = response.body()!!
                    authUseCase.saveAuthToken(authBody.token, authBody.rol)
                    authUseCase.refreshAccessTime()

                    val isAdmin = authUseCase.isAdmin(authBody.rol)
                    _authState.value = AuthState.Success(isAdmin, authBody.rol)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error en el registro"
                    _authState.value = AuthState.Error(errorMsg)
                }

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
        onAuthenticated: (isAdmin: Boolean, rol: String) -> Unit,
        onNotAuthenticated: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val isAuthenticated = authUseCase.isUserAuthenticated()
                if (isAuthenticated) {
                    authUseCase.refreshAccessTime()
                    val rol = authUseCase.getUserRol() ?: "usuario"
                    val isAdmin = authUseCase.isAdmin(rol)
                    onAuthenticated(isAdmin, rol)
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
    suspend fun logout() = authUseCase.logout()
}