package com.example.mercader.ui.screens.games

import com.example.mercader.domain.models.Game

data class CollectionState (
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    )