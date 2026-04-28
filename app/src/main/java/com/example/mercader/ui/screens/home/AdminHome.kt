package com.example.mercader.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercader.common.components.InProgressModal
import com.example.mercader.common.components.SidebarMenu

@Composable
fun AdminHome(
    onNavigateToGameForm: () -> Unit,
    onNavigateToStock: () -> Unit,
    onSwitchToUser: () -> Unit,
) {
    var sidebarVisible by remember { mutableStateOf(false) }
    var showInProgressModal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            AdminHeader(
                onBurgerClick = { sidebarVisible = true }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Cliente en Local",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Divider()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "En proceso...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // camviar vista
                    Button(
                        onClick = { onSwitchToUser() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Cambiar Vista")
                    }
                    OutlinedButton(
                        onClick = { showInProgressModal = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Juegos en Mesa")
                    }

                    OutlinedButton(
                        onClick = { showInProgressModal = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Agregar cliente al local")
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = sidebarVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            )
        ) {
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
                onSellos = { showInProgressModal = true },
                onOfertas = { showInProgressModal = true }
            )
        }

        if (showInProgressModal) {
            InProgressModal(onDismiss = { showInProgressModal = false })
        }
    }
}

@Composable
private fun AdminHeader(onBurgerClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo placeholder — reemplaza con Image(painterResource(R.drawable.logo))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "M",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            // Cuando tengas el SVG:
            // Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Adm. Mercader",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Burger button — reemplaza con Image(painterResource(R.drawable.ic_burger))
            IconButton(onClick = onBurgerClick) {
                Text(
                    text = "☰",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp
                )
                // Con SVG:
                // Icon(painter = painterResource(id = R.drawable.ic_burger), contentDescription = "Menú")
            }
        }
    }
}