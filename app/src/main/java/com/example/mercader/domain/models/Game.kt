package com.example.mercader.domain.models

import com.example.mercader.data.remote.models.Difficulty
import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Editorial

data class Game(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val tutorial: String = "",
    val category: Category,
    val nMinPerson: Int = 0,
    val nMaxPerson: Int = 0,
    val minMinutes: Int = 0,
    val maxMinutes: Int = 0,
    val difficulty: Difficulty,
    val editorial: Editorial,
    val stock: Int = 0,
    val price: Float = 0f
)

