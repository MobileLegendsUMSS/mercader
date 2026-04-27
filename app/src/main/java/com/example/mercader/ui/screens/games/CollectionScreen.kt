package com.example.mercader.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mercader.common.components.GameCard
import com.example.mercader.common.components.GameDetailDialog
import com.example.mercader.domain.models.Game

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var selectedGame by remember { mutableStateOf<Game?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.games) { game ->
            GameCard(
                game = game,
                onClick = { selectedGame = game }
            )
        }
    }

    selectedGame?.let { game ->
        GameDetailDialog(
            game = game,
            onDismiss = { selectedGame = null }
        )
    }
}