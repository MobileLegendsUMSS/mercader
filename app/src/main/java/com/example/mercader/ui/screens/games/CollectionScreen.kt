package com.example.mercader.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mercader.common.components.BackButton
import com.example.mercader.common.components.GameCard
import com.example.mercader.common.components.GameDetailDialog
import com.example.mercader.domain.models.Game

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEditGame: ((Game) -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Colección de Juegos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            BackButton(onClick = onBack)

        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.games) { game ->
                GameCard(
                    game = game,
                    onClick = { selectedGame = game },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
        selectedGame?.let { game ->
            GameDetailDialog(
                game = game,
                onDismiss = { selectedGame = null },
                onEditGame = { gameToEdit  ->
                    selectedGame  = null
                    onEditGame?.invoke(gameToEdit)
                }
            )
    }
}
