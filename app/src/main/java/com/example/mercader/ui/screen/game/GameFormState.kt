package com.example.mercader.ui.screen.game
import com.example.mercader.data.remote.models.*
data class GameFormState(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val tutorial: String = "",
    val category: Category = Category("",""),
    val nMinPerson: Int = 0,
    val nMaxPerson: Int = 0,
    val minMinutes: Int = 0,
    val maxMinutes: Int = 0,
    val difficulty: Difficulty = Difficulty("",""),
    val editorial: Editorial = Editorial("",""),
    val stock: Int = 0,
    val price: Float = 0f,

    val gameCategories: List<Category> = emptyList(),
    val difficulties: List<Difficulty> = emptyList(),
    val editorials: List<Editorial> = emptyList(),

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)