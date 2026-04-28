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
import androidx.compose.foundation.clickable

@Composable
fun UserHome(
    onSwitchToAdmin: () -> Unit,
) {
    var showInProgressModal by remember { mutableStateOf(false) }

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
            onSwitchToAdmin = onSwitchToAdmin
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
            // Con SVG: Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")
        }

        Text(
            text = "Adm. Mercader",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Burger — aquí puedes agregar el burger igual que en Admin si lo necesitas
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

@Composable
private fun UserBottomNav(
    onInProgress: () -> Unit,
    onSwitchToAdmin: () -> Unit,
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavIconButton(
                icon = "🏠",  // reemplaza: painterResource(R.drawable.ic_home)
                label = "Inicio",
                onClick = onInProgress
            )
            NavIconButton(
                icon = "🧭",  // reemplaza: painterResource(R.drawable.ic_brujula)
                label = "Explorar",
                onClick = onInProgress
            )
            NavIconButton(
                icon = "🛒",  // reemplaza: painterResource(R.drawable.ic_carrito)
                label = "Carrito",
                onClick = onInProgress
            )
            NavIconButton(
                icon = "👤",  // reemplaza: painterResource(R.drawable.ic_user)
                label = "Perfil",
                onClick = onInProgress
            )

            // cambiar a Admin
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onSwitchToAdmin() }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "⚙️", fontSize = 20.sp)
                Text(
                    text = "Admin",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NavIconButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}