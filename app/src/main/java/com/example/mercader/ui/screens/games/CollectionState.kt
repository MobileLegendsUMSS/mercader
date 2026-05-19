package com.example.mercader.ui.screens.games

import com.example.mercader.common.utils.GameFilters
import com.example.mercader.domain.models.Game

data class CollectionState (
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeFilters: GameFilters? = null,
    val hasActiveFilters: Boolean = false,
    val filtersSummary: String = "Sin filtros aplicados"
)