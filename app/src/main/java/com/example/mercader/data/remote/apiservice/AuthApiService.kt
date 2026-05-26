package com.example.mercader.data.remote.apiservice

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// DTOs locales que usa la interfaz
data class LoginRequest(
    val nombre: String,
    val contrasenna: String
)

data class SigninRequest(
    val nombre: String,
    val contrasenna: String,
    val nombres: String?,
    val apellidos: String?,
    val telefono: String,
    val correo_contacto: String
)

data class AuthResponse(
    val mensaje: String,
    val token: String,
    val usuario: UsuarioResponse
)

data class UsuarioResponse(
    val id: String,
    val nombre: String
)

interface AuthApiService {

    // Iniciar sesión (Se concatena automáticamente: BASE_URL + auth/login)
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    // Registro de usuarios
    @POST("auth/signin")
    suspend fun signin(
        @Body request: SigninRequest
    ): Response<AuthResponse>
}