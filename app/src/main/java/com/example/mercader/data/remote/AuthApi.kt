package com.example.mercader.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val nombre: String,
    val contrasenna: String
)

// 🆕 Se quedan opcionales con valor nulo por defecto
data class SigninRequest(
    val nombre: String,
    val contrasenna: String,
    val nombres: String? = null,
    val apellidos: String? = null,
    val telefono: String? = null,
    val correo_contacto: String? = null
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

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/signin")
    suspend fun signin(
        @Body request: SigninRequest
    ): Response<AuthResponse>
}