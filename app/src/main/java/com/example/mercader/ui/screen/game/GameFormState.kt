package com.example.mercader.ui.screen.game

data class GameFormState(
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
    val price: Float = 0f,

    val gameCategories: List<String> = emptyList(),
    val difficulties: List<String> = emptyList(),
    val editorials: List<String> = emptyList(),

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)