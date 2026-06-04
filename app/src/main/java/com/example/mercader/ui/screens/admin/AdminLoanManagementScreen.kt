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
import com.example.mercader.domain.models.UserLoan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminLoanManagementScreen(
    onBack: () -> Unit,
    viewModel: AdminLoanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var selectedLoan by remember { mutableStateOf<UserLoan?>(null) }

    val tabs = listOf("Pendientes", "Recogidos", "Devueltos")
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> viewModel.loadLoans(vigente = true, recogido = false, devuelto = false)
            1 -> viewModel.loadLoans(vigente = false, recogido = true, devuelto = false)
            2 -> viewModel.loadLoans(vigente = false, recogido = false, devuelto = true)
        }
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
                    text = "Gestión de Préstamos",
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
                    onClick = { selectedTabIndex = index },
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
            state.isLoading && state.loans.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.errorMessage != null && state.loans.isEmpty() -> {
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
                        Button(onClick = { viewModel.loadLoans(true, false, false) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            state.loans.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📋", fontSize = 64.sp)
                        Text(
                            text = when (selectedTabIndex) {
                                0 -> "No hay préstamos pendientes"
                                1 -> "No hay préstamos recogidos"
                                else -> "No hay préstamos devueltos"
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
                    items(state.loans) { loan ->
                        AdminLoanCard(
                            loan = loan,
                            onClick = { selectedLoan = loan }
                        )
                    }
                }
            }
        }
    }

    // Modal de detalle
    selectedLoan?.let { loan ->
        AdminLoanDetailModal(
            loan = loan,
            onDismiss = { selectedLoan = null },
            onConfirmPickup = { fechaInicio ->
                viewModel.updateLoan(loan.loanId, fechaInicio, null) { success ->
                    if (success) {
                        selectedLoan = null
                        when (selectedTabIndex) {
                            0 -> viewModel.loadLoans(vigente = true, recogido = false, devuelto = false)
                            1 -> viewModel.loadLoans(vigente = false, recogido = true, devuelto = false)
                            2 -> viewModel.loadLoans(vigente = false, recogido = false, devuelto = true)
                        }
                    }
                }
            },
            onConfirmReturn = { fechaFin ->
                viewModel.updateLoan(loan.loanId, null, fechaFin) { success ->
                    if (success) {
                        selectedLoan = null
                        when (selectedTabIndex) {
                            0 -> viewModel.loadLoans(vigente = true, recogido = false, devuelto = false)
                            1 -> viewModel.loadLoans(vigente = false, recogido = true, devuelto = false)
                            2 -> viewModel.loadLoans(vigente = false, recogido = false, devuelto = true)
                        }
                    }
                }
            }
        )
    }
}

// Función auxiliar para formatear fechas
fun formatDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "No registrada"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}