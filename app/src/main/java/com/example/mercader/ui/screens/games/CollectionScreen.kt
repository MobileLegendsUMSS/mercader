// ui/screens/games/CollectionScreen.kt
package com.example.mercader.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
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
import com.example.mercader.common.utils.GameFilters
import com.example.mercader.common.utils.GameFilter

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEditGame: ((Game) -> Unit)? = null,
    initialSearchQuery: String = "",
    initialFilters: GameFilters? = null
) {
    val state by viewModel.state.collectAsState()
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var searchQuery by remember { mutableStateOf(initialSearchQuery) }

    LaunchedEffect(initialFilters) {
        if (initialFilters != null && GameFilter.hasActiveFilters(initialFilters)) {
            viewModel.updateFilters(initialFilters)
        }
    }

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
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackButton(onClick = onBack)

            Text(
                text = "Colección de Juegos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            if (state.hasActiveFilters) {
                IconButton(
                    onClick = { viewModel.clearFilters() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Limpiar filtros"
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Buscar por nombre o categoría",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else if (state.errorMessage != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${state.errorMessage}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.refreshGames() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
        else if (filteredGames.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (searchQuery.isNotEmpty())
                            "No se encontraron juegos para \"$searchQuery\""
                        else if (state.hasActiveFilters)
                            "No hay juegos que coincidan con los filtros seleccionados"
                        else
                            "No hay juegos disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (state.hasActiveFilters) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.clearFilters() }) {
                            Text("Limpiar filtros")
                        }
                    }
                }
            }
        }
        else {
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