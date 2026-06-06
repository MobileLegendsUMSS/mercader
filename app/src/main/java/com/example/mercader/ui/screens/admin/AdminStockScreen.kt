// ui/screens/admin/AdminStockScreen.kt
package com.example.mercader.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.common.components.BackButton
import com.example.mercader.common.components.GameCard
import com.example.mercader.common.components.GameDetailDialog
import com.example.mercader.common.components.SearchBar
import com.example.mercader.domain.models.Game
import com.example.mercader.ui.screens.games.CollectionViewModel

// Tipos de filtro rápido
enum class StockFilterType(val displayName: String) {
    ALL("TODO"),
    SALE("VENTA"),
    RENT("ALQUILER"),
    LOAN("PRÉSTAMO")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStockScreen(
    onBack: () -> Unit,
    onEditGame: (Game) -> Unit,
    onDeleteGame: (Game) -> Unit,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(StockFilterType.ALL) }

    // Cargar juegos al inicio
    LaunchedEffect(Unit) {
        viewModel.loadGames()
    }

    // Filtrar juegos según búsqueda y tipo de servicio
    val filteredGames = remember(state.games, searchQuery, selectedFilter) {
        var result = state.games

        // Filtro por búsqueda
        if (searchQuery.isNotEmpty()) {
            result = result.filter { game ->
                game.title.contains(searchQuery, ignoreCase = true) ||
                        game.category.descripcion.contains(searchQuery, ignoreCase = true)
            }
        }

        // Filtro por tipo de servicio
        result = when (selectedFilter) {
            StockFilterType.ALL -> result
            StockFilterType.SALE -> result.filter { it.isPurchaseAvailable }
            StockFilterType.RENT -> result.filter { it.isRentAvailable }
            StockFilterType.LOAN -> result.filter { it.isLoanAvailable }
        }

        result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header con botón de volver
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackButton(onClick = onBack)
            Text(
                text = "Gestión de Stock",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Barra de búsqueda
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Buscar por nombre o categoría...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Navegación de filtros rápidos (TODO, VENTA, ALQUILER, PRÉSTAMO)
        ScrollableTabRow(
            selectedTabIndex = selectedFilter.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            StockFilterType.values().forEachIndexed { index, filter ->
                Tab(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    text = {
                        Text(
                            text = filter.displayName,
                            fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Contenido: lista de juegos o estados de carga/error
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${state.errorMessage}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refreshGames() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            filteredGames.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            searchQuery.isNotEmpty() -> "No se encontraron juegos para \"$searchQuery\""
                            selectedFilter != StockFilterType.ALL -> "No hay juegos disponibles para ${selectedFilter.displayName}"
                            else -> "No hay juegos disponibles"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
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
    }

    selectedGame?.let { game ->
        GameDetailDialog(
            game = game,
            cartManager = null,
            reserveManager = null,
            onDismiss = { selectedGame = null },
            onEditGame = onEditGame,
            onDeleteGame = onDeleteGame,
            isAdminMode = true
        )
    }
}