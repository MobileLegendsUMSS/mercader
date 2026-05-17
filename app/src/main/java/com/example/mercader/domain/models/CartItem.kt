package com.example.mercader.domain.models

data class CartItem(
    val game: Game,
    var quantity: Int = 1
)