package com.example.mercader.data.remote.models

data class CarouselRequestDTO(
    val allGames: Boolean,
    val order: String,
    val amount: Int? = null
)
