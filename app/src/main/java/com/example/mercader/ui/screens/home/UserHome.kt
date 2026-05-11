package com.example.mercader.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercader.common.components.InProgressModal
import com.example.mercader.common.components.SearchBarWithButton
import com.example.mercader.common.components.UserBottomNav
import com.example.mercader.ui.screens.games.CollectionViewModel
import com.example.mercader.ui.screens.games.CollectionScreen

@Composable
fun UserHome(
    onSwitchToAdmin: () -> Unit,
    collectionViewModel: CollectionViewModel
) {
    var showInProgressModal by remember { mutableStateOf(false) }
    var showCollectionScreen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    if (showCollectionScreen) {
        CollectionScreen(
            viewModel = collectionViewModel,
            initialSearchQuery = searchQuery,
            onBack = {
                showCollectionScreen = false
                searchQuery = ""
            }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {

        UserHeader()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Barra de búsqueda con botón
            SearchBarWithButton(
                onSearch = { query ->  
                    searchQuery = query
                    showCollectionScreen = true
                },
                placeholder = "Buscar ...",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            CarouselSection(title = "Condiciones")
            Divider()
            CarouselSection(title = "Mis Preferencias")
            Divider()
            CarouselSection(title = "Visitados")
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        UserBottomNav(
            onInProgress = { showInProgressModal = true },
            onSwitchToAdmin = onSwitchToAdmin,
            onSearch = {
                searchQuery = ""
                showCollectionScreen = true
            }
        )
    }

    if (showInProgressModal) {
        InProgressModal(onDismiss = { showInProgressModal = false })
    }
}

@Composable
private fun UserHeader() {
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
            text = "Adm. Mercader",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(44.dp))
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