package com.example.mercader.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.common.components.BackButton
import com.example.mercader.data.remote.models.AdminPurchaseItemDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPurchaseManagementScreen(
    onBack: () -> Unit,
    viewModel: AdminPurchaseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var selectedPurchase by remember { mutableStateOf<AdminPurchaseItemDTO?>(null) }

    val tabs = listOf("Pendientes", "Aceptadas", "Rechazadas", "Todas")
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Filtrar compras según la pestaña seleccionada
    val filteredPurchases = remember(state.purchases, selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> state.purchases.filter { it.estado == "pendiente" }
            1 -> state.purchases.filter { it.estado == "aceptado" }
            2 -> state.purchases.filter { it.estado == "rechazado" }
            else -> state.purchases
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadPurchases()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = onBack)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Gestión de Compras",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        viewModel.clearPurchases()
                        viewModel.loadPurchases()
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Contenido
        when {
            state.isLoading && state.purchases.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.errorMessage != null && filteredPurchases.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "❌", fontSize = 48.sp)
                        Text(
                            text = state.errorMessage!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadPurchases() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            filteredPurchases.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🛒", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (selectedTabIndex) {
                                0 -> "No hay compras pendientes"
                                1 -> "No hay compras aceptadas"
                                2 -> "No hay compras rechazadas"
                                else -> "No hay compras registradas"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPurchases) { purchase ->
                        PurchaseCard(
                            purchase = purchase,
                            onClick = { selectedPurchase = purchase }
                        )
                    }
                }
            }
        }
    }

    // Modal de detalle
    selectedPurchase?.let { purchase ->
        AdminPurchaseDetailModal(
            purchase = purchase,
            onDismiss = { selectedPurchase = null },
            onConfirmAccept = {
                viewModel.updatePurchaseState(purchase.idCompra, "aceptado") { success ->
                    if (success) {
                        selectedPurchase = null
                        viewModel.loadPurchases()
                    }
                }
            },
            onConfirmReject = {
                viewModel.updatePurchaseState(purchase.idCompra, "rechazado") { success ->
                    if (success) {
                        selectedPurchase = null
                        viewModel.loadPurchases()
                    }
                }
            }
        )
    }
}