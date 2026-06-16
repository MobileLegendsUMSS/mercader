package com.example.mercader.ui.screens.home
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.mercader.common.components.SidebarMenu

@Composable
fun AdminHome(
    onNavigateToGameForm: () -> Unit,
    onNavigateToStock: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLoanManagement: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToManageUsers: () -> Unit,
    onNavigateToAttributes: () -> Unit,
    isSuperAdmin: Boolean = false
) {
    var sidebarVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Header con logo y título
            AdminHeader(
                onMenuClick = { sidebarVisible = true }
            )

            // Contenido principal
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // Tarjeta de bienvenida
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = if (isSuperAdmin) "👑 Hola, Super Administrador" else "📋 Hola, Administrador",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bienvenido a tu panel de control. Aquí encontrarás todas las herramientas para gestionar tu ludoteca.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de gestión
                Text(
                    text = "📌 Gestión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Opciones de gestión
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GestionOption(
                        emoji = "+🎮",
                        title = "Nuevo Juego",
                        description = "Agregar un juego a la ludoteca",
                        onClick = onNavigateToGameForm
                    )

                    GestionOption(
                        emoji = "📦",
                        title = "Stock",
                        description = "Controlar el inventario de juegos disponibles",
                        onClick = onNavigateToStock
                    )

                    GestionOption(
                        emoji = "🔄",
                        title = "Prestamos",
                        description = "Gestionar préstamos y alquileres activos",
                        onClick = onNavigateToLoanManagement
                    )

                    GestionOption(
                        emoji = "💰",
                        title = "Compras",
                        description = "Ver historial y gestionar compras",
                        onClick = onNavigateToPurchases
                    )

                    GestionOption(
                        emoji = "📊",
                        title = "Reportes",
                        description = "Ver estadísticas y reportes de la ludoteca",
                        onClick = onNavigateToReports
                    )

                    GestionOption(
                        emoji = "👤",
                        title = "Mi Perfil",
                        description = "Configurar información personal",
                        onClick = onNavigateToProfile
                    )

                    GestionOption(
                        emoji = "🏷️",
                        title = "Categorías y Editoriales",
                        description = "Gestionar categorías y editoriales del sistema",
                        onClick = onNavigateToAttributes
                    )

                    // Solo superadmin puede gestionar usuarios
                    if (isSuperAdmin) {
                        GestionOption(
                            emoji = "👥",
                            title = "Gestionar Usuarios",
                            description = "Cambiar roles y administrar usuarios",
                            onClick = onNavigateToManageUsers,
                            isSuperAdmin = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Sidebar menu (solo visible cuando se abre)
        if (sidebarVisible) {
            SidebarMenu(
                onClose = { sidebarVisible = false },
                onNewGame = {
                    sidebarVisible = false
                    onNavigateToGameForm()
                },
                onStock = {
                    sidebarVisible = false
                    onNavigateToStock()
                },
                onProfile = {
                    sidebarVisible = false
                    onNavigateToProfile()
                },
                onLoanManagement = {
                    sidebarVisible = false
                    onNavigateToLoanManagement()
                },
                onReport = {
                    sidebarVisible = false
                    onNavigateToReports()
                },
                onPurchases = {
                    sidebarVisible = false
                    onNavigateToPurchases()
                },
                onManageUsers = onNavigateToManageUsers,
                onAttributes = {
                    sidebarVisible = false
                    onNavigateToAttributes()
                },
                isSuperAdmin = isSuperAdmin
            )
        }
    }
}

@Composable
fun GestionOption(
    emoji: String,
    title: String,
    description: String,
    onClick: () -> Unit,
    isSuperAdmin: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuperAdmin)
                MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Emoji
            Text(
                text = emoji,
                fontSize = 36.sp
            )

            // Textos
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Flecha indicadora
            Text(
                text = "→",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AdminHeader(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo y título
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎲",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp
                )
            }

            Text(
                text = "Mercader - Ludoteca",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}