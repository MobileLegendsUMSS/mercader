package com.example.mercader.domain.models

data class Game(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val tutorial: String = "",
    val category: String = "",
    val nMinPerson: Int = 0,
    val nMaxPerson: Int = 0,
    val minMinutes: Int = 0,
    val maxMinutes: Int = 0,
    val difficulty: String = "",
    val editorial: String = "",
    val stock: Int = 0,
    val price: Float = 0f
)

