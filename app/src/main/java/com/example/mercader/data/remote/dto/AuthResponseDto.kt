package com.example.mercader.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    val mensaje: String,
    val token: String,
    val usuario: UsuarioDto
)