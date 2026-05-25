package com.example.mercader.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(
    val nombre: String,
    val contrasenna: String
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
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/api/signin")
    suspend fun signin(@Body request: LoginRequest): AuthResponse
}
