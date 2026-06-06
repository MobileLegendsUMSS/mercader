package com.example.mercader.data.remote.apiservice

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// DTOs actualizados con refresh token
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
    val accessToken: String,  // ? Cambiado de token a accessToken
    val refreshToken: String, // ? NUEVO
    val usuario: UsuarioResponse,
    val rol: String
)

// ? NUEVO: Request para refrescar token
data class RefreshTokenRequest(
    val refreshToken: String
)

// ? NUEVO: Respuesta al refrescar
data class RefreshTokenResponse(
    val success: Boolean,
    val accessToken: String,
    val refreshToken: String,
    val rol: String? = null
)

data class UsuarioResponse(
    val id: String,
    val nombre: String
)

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/signin")
    suspend fun signin(
        @Body request: SigninRequest
    ): Response<AuthResponse>

    // ? NUEVO: Endpoint para refrescar token
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    // ? NUEVO: Endpoint para logout
    @POST("auth/logout")
    suspend fun logout(
        @Body request: RefreshTokenRequest
    ): Response<Unit>
}