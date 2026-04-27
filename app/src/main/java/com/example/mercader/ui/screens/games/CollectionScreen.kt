package com.example.mercader.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
    val games by viewModel.games.collectAsState()
    var selectedGame by remember { mutableStateOf<Game?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(games) { game ->
            GameCard(
                game = game,
                onClick = { selectedGame = game },
                modifier = Modifier.fillMaxWidth()
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