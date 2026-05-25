package com.example.mercader.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.common.components.InProgressModal
import com.example.mercader.common.components.SearchBarWithButton
import com.example.mercader.common.components.UserBottomNav
import com.example.mercader.common.utils.CartManager
import com.example.mercader.common.utils.GameFilters
import com.example.mercader.common.utils.ReserveManager
import com.example.mercader.ui.screens.games.*
import kotlinx.coroutines.launch

@Composable
fun UserHome(
    onSwitchToAdmin: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    cartManager: CartManager,
    reserveManager: ReserveManager,
    collectionViewModel: CollectionViewModel = hiltViewModel(),
    filterViewModel: FilterViewModel = hiltViewModel()
) {
    var showInProgressModal by remember { mutableStateOf(false) }
    var showCollectionScreen by remember { mutableStateOf(false) }
    var showFilterScreen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var appliedFilters by remember { mutableStateOf<GameFilters?>(null) }

    // Estado para el contador del carrito
    var cartItemCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Función para refrescar el carrito
    fun refreshCart() {
        coroutineScope.launch {
            cartItemCount = cartManager.getTotalItemCount()
        }
    }

    // Cargar contador inicial
    LaunchedEffect(Unit) {
        refreshCart()
    }

    if (showCollectionScreen) {
        CollectionScreen(
            viewModel = collectionViewModel,
            cartManager = cartManager,
            initialSearchQuery = searchQuery,
            initialFilters = appliedFilters,
            onBack = {
                showCollectionScreen = false
                appliedFilters = null
                searchQuery = ""
                filterViewModel.clearAllFilters()
                refreshCart()
            },
            onNavigateToCart = onNavigateToCart,
            onCartUpdate = { refreshCart() },
            reserveManager = reserveManager
        )
        return
    }

    if (showFilterScreen) {
        FilterScreen(
            viewModel = filterViewModel,
            onApplyFilters = {
                appliedFilters = filterViewModel.getActiveFilters()
                showFilterScreen = false
                showCollectionScreen = true
            },
            onClearFilters = {
                filterViewModel.clearAllFilters()
                appliedFilters = null
                showFilterScreen = false
                showCollectionScreen = true
            },
            onClose = {
                showFilterScreen = false
            }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {

        UserHeader(
            onFilterClick = { showFilterScreen = true },
            cartItemCount = cartItemCount,
            onCartClick = onNavigateToCart
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchBarWithButton(
                onSearch = { query ->
                    searchQuery = query
                    appliedFilters = null
                    showCollectionScreen = true
                },
                placeholder = "Buscar ...",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            CarouselSection(title = "Condiciones")
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            CarouselSection(title = "Mis Preferencias")
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            CarouselSection(title = "Visitados")
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(8.dp))
        }

        UserBottomNav(
            onInProgress = { showInProgressModal = true },
            onSwitchToAdmin = onSwitchToAdmin,
            onSearch = {
                searchQuery = ""
                appliedFilters = null
                showCollectionScreen = true
            },
            onCartClick = onNavigateToCart,
            onProfileClick = onNavigateToProfile
        )
    }

    if (showInProgressModal) {
        InProgressModal(onDismiss = { showInProgressModal = false })
    }
}

@Composable
private fun UserHeader(
    onFilterClick: () -> Unit,
    cartItemCount: Int,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "M",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }

        Text(
            text = "Mercader Games",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón del carrito con badge
            Box {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Ver Carrito",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // Badge con el número de items
                if (cartItemCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.error),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                            color = MaterialTheme.colorScheme.onError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Botón de filtro
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Filtrar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun CarouselSection(title: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Carrusel $title",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "(vacío — en proceso)",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}