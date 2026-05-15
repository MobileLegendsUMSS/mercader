package com.example.mercader.ui.screens.games

import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Difficulty
import com.example.mercader.data.remote.models.Editorial


data class FilterState(
    // Filtros seleccionados
    val selectedCategory: Category? = null,
    val nMinPerson: Int = 1,
    val nMaxPerson: Int = 8,
    val minMinutes: Int = 0,
    val maxMinutes: Int = 240,
    val selectedDifficulty: Difficulty? = null,
    val selectedEditorial: Editorial? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,

    // Listas de opciones disponibles
    val gameCategories: List<Category> = emptyList(),
    val difficulties: List<Difficulty> = emptyList(),
    val editorials: List<Editorial> = emptyList(),

    // Estado de carga
    val isLoading: Boolean = false,
    val hasActiveFilters: Boolean = false,
    val errorMessage: String? = null
)