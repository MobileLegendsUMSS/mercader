package com.example.mercader.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mercader.ui.screens.admin.viewmodel.AdminRoleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRoleManagementScreen(
    onBack: () -> Unit,
    onRoleChanged: () -> Unit,
    viewModel: AdminRoleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestión de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.users.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No hay usuarios registrados")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadUsers() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.users) { user ->
                            UserRoleCard(
                                user = user,
                                isUpdating = viewModel.isUpdating(user.id),
                                onRoleChange = { newRole ->
                                    viewModel.changeRole(user.id, newRole) { success ->
                                        if (success) {
                                            onRoleChanged()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Snackbar para mensajes de error
            uiState.errorMessage?.let { message ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(message)
                }
            }
        }
    }
}

@Composable
fun UserRoleCard(
    user: com.example.mercader.ui.screens.admin.model.UserRoleItem,
    isUpdating: Boolean,
    onRoleChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("usuario", "admin")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    if (!user.nombres.isNullOrBlank()) {
                        Text(
                            text = "${user.nombres} ${user.apellidos ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = user.correo_contacto,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    Button(
                        onClick = { expanded = true },
                        enabled = !isUpdating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.rol == "admin")
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(if (user.rol == "admin") "👑 Admin" else "👤 Usuario")
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (role == "admin") "Ascender a Admin" else "Degradar a Usuario",
                                        color = if (role == "admin")
                                            MaterialTheme.colorScheme.tertiary
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    onRoleChange(role)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}