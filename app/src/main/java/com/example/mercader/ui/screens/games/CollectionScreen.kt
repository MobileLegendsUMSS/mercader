// ui/screens/games/CollectionScreen.kt
package com.example.mercader.ui.screens.games

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.mercader.common.components.BackButton
import com.example.mercader.common.components.GameCard
import com.example.mercader.common.components.GameDetailDialog
import com.example.mercader.common.components.SearchBar
import com.example.mercader.domain.models.Game
import com.example.mercader.common.utils.GameFilters
import com.example.mercader.common.utils.GameFilter
import com.example.mercader.common.utils.CartManager
import com.example.mercader.common.utils.ReserveManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.net.toUri

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    cartManager: CartManager,
    reserveManager: ReserveManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEditGame: ((Game) -> Unit)? = null,
    onNavigateToCart: (() -> Unit)? = null,
    onCartUpdate: (() -> Unit)? = null,
    onNavigateToReviews: ((Game) -> Unit)? = null,
    initialSearchQuery: String = "",
    initialFilters: GameFilters? = null
) {
    val state by viewModel.state.collectAsState()
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var searchQuery by remember { mutableStateOf(initialSearchQuery) }
    val favoriteViewModel: FavoriteViewModel = hiltViewModel()
    val context = LocalContext.current
    LaunchedEffect(initialFilters) {
        if (initialFilters != null && GameFilter.hasActiveFilters(initialFilters)) {
            viewModel.updateFilters(initialFilters)
        }
    }
    LaunchedEffect(Unit) {
        favoriteViewModel.loadFavorites()
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
    val isAdmin = onEditGame != null
    val visibleGames = remember(filteredGames, isAdmin) {
        if (isAdmin) {
            filteredGames
        } else {
            filteredGames.filter { game ->
                game.stock > 0 && game.active
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
                items(visibleGames) { game ->
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
            cartManager = cartManager,
            reserveManager = reserveManager,
            onDismiss = {
                selectedGame = null
                favoriteViewModel.loadFavorites()
            },
            onTutorial = {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(game.tutorial)
                    )
                    context.startActivity(intent)
                } catch (e: Exception) {
                }
            },
            onEditGame = { gameToEdit ->
                selectedGame = null
                onEditGame?.invoke(gameToEdit)
            },
            onNavigateToCart = onNavigateToCart,
            onCartUpdate = onCartUpdate,
            onNavigateToReviews = { gameForReviews ->
                selectedGame = null
                onNavigateToReviews?.invoke(gameForReviews)
            },
        )
    }
}