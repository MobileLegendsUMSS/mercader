// ui/screens/games/CollectionScreen.kt
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
import com.example.mercader.common.components.BackButton
import com.example.mercader.common.components.GameCard
import com.example.mercader.common.components.GameDetailDialog
import com.example.mercader.common.components.SearchBar
import com.example.mercader.domain.models.Game

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEditGame: ((Game) -> Unit)? = null,
    initialSearchQuery: String = ""
) {
    val state by viewModel.state.collectAsState()
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var searchQuery by remember { mutableStateOf(initialSearchQuery) } 
    val filteredGames = remember(state.games, searchQuery) {
        if (searchQuery.isEmpty()) {
            state.games
        } else {
            state.games.filter { game ->
                game.title.contains(searchQuery, ignoreCase = true) ||
                        game.category.descripcion.contains(searchQuery, ignoreCase = true)
            }
        }
    }

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

        // Barra de búsqueda
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Buscar por nombre o categoria",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (filteredGames.isEmpty() && searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron juegos para \"$searchQuery\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Grid de juegos filtrados
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGames) { game ->
                    GameCard(
                        game = game,
                        onClick = { selectedGame = game },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Diálogo de detalle del juego
    selectedGame?.let { game ->
        GameDetailDialog(
            game = game,
            onDismiss = { selectedGame = null },
            onEditGame = { gameToEdit ->
                selectedGame = null
                onEditGame?.invoke(gameToEdit)
            }
        )
    }
}
