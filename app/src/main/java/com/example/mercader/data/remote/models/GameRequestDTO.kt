package com.example.mercader.data.remote.models

data class GameRequestDTO(
    val title: String,
    val description: String,
    val tutorial: String,
    val category: String,
    val nMinPerson: Int,
    val nMaxPerson: Int,
    val minMinutes: Int,
    val maxMinutes: Int,
    val difficulty: String,
    val editorial: String,
    val stock: Int,
    val price: Float
)