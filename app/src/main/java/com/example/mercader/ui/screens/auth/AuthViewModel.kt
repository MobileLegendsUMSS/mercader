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
    data class Success(val isAdmin: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authApiService: AuthApiService, // se usa el nuevo servicio aqui
    private val authUseCase: AuthenticationUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(nombre: String, contrasenna: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // 1. corregido: ahora llama a authapiaservice
                val response = authApiService.login(
                    LoginRequest(nombre = nombre, contrasenna = contrasenna)
                )

                // 2. validar si la respuesta del servidor es exitosa
                if (response.isSuccessful && response.body() != null) {
                    val authBody = response.body()!!

                    // 3. guardar token en cache
                    authUseCase.saveAuthToken(authBody.token)

                    // 4. resetear el contador de 15 dias
                    authUseCase.refreshAccessTime()

                    // 5. determinar si es admin por el sufijo
                    val isAdmin = contrasenna.endsWith("#adm")

                    // 6. navegar al home
                    _authState.value = AuthState.Success(isAdmin)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "usuario o contrasenna incorrectos"
                    _authState.value = AuthState.Error(errorMsg)
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "error en la autenticacion"
                )
            }
        }
    }

    // se actualiza la funcion signin para recibir todos los parametros requeridos por el backend
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

                // 1. corregido: ahora se pasan todas las propiedades recolectadas al signinrequest
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

                // 2. validar respuesta exitosa
                if (response.isSuccessful && response.body() != null) {
                    val authBody = response.body()!!
                    authUseCase.saveAuthToken(authBody.token)
                    authUseCase.refreshAccessTime()
                    val isAdmin = contrasenna.endsWith("#adm")
                    _authState.value = AuthState.Success(isAdmin)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "error en el registro"
                    _authState.value = AuthState.Error(errorMsg)
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "error en el registro"
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
                    onAuthenticated(false) // todo: obtener isadmin del token
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