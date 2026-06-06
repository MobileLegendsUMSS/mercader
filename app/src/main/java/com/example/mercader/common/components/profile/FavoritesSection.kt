package com.example.mercader.common.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mercader.common.components.GameCard
import com.example.mercader.common.components.GameDetailDialog
import com.example.mercader.common.utils.CartManager
import com.example.mercader.common.utils.ReserveManager
import com.example.mercader.domain.models.Game
import com.example.mercader.ui.screens.profile.ProfileState
import com.example.mercader.ui.screens.profile.ProfileViewModel


@Composable
 fun FavoritesSection(
    state: ProfileState,
    viewModel: ProfileViewModel,
    cartManager: CartManager,
    reserveManager: ReserveManager,
) {
    var currentPage by remember { mutableStateOf(0) }
    var selectedGame by remember { mutableStateOf<Game?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.isFavoritesLoading && state.favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.favorites.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes juegos favoritos. ¡Añade algunos desde la colección!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val pageSize = 3
            val totalPages = (state.favorites.size + pageSize - 1) / pageSize

            LaunchedEffect(state.favorites.size) {
                if (currentPage >= totalPages) {
                    currentPage = maxOf(0, totalPages - 1)
                }
            }

            val startIndex = currentPage * pageSize
            val endIndex = minOf(startIndex + pageSize, state.favorites.size)
            val currentPageGames = state.favorites.subList(startIndex, endIndex)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentPageGames.forEach { game ->
                    GameCard(
                        game = game,
                        onClick = { selectedGame = game },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(pageSize - currentPageGames.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            if (totalPages > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (currentPage > 0) currentPage-- },
                        enabled = currentPage > 0
                    ) {
                        Text("◀", color = if (currentPage > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(totalPages) { index ->
                            val isSelected = index == currentPage
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }

                    IconButton(
                        onClick = { if (currentPage < totalPages - 1) currentPage++ },
                        enabled = currentPage < totalPages - 1
                    ) {
                        Text("▶", color = if (currentPage < totalPages - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                    }
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
                viewModel.loadFavorites()
            },
            //isAdmin=false
        )
    }
}