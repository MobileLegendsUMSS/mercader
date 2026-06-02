package com.example.mercader.ui.screens.home

import com.example.mercader.domain.models.Game

data class HomeState(
    val recentGames: List<Game> = emptyList(),
    val mostVisitedGames: List<Game> = emptyList(),
    val mostSoldGames: List<Game> = emptyList(),
    val mostBorrowedGames: List<Game> = emptyList(),
    val isLoadingRecent: Boolean = false,
    val isLoadingVisited: Boolean = false,
    val isLoadingSold: Boolean = false,
    val isLoadingBorrowed: Boolean = false,
    val error: String? = null
)
