package com.example.mercader.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SidebarMenu(
    onClose: () -> Unit,
    onNewGame: () -> Unit,
    onStock: () -> Unit,
    onSellos: () -> Unit,
    onOfertas: () -> Unit,
    onProfile: () -> Unit,  // ← Nueva opción
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.70f)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Parte superior del menú
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gestión de Ludoteca",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onClose) {
                        Text(
                            text = "✕",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Divider()

                Spacer(modifier = Modifier.height(8.dp))

                SidebarButton(text = "Nuevo Juego +", onClick = onNewGame, primary = true)
                SidebarButton(text = "Stock", onClick = onStock)
                SidebarButton(text = "Sellos de Clientes", onClick = onSellos)
                SidebarButton(text = "Ofertas %", onClick = onOfertas)
            }

            // Parte inferior del menú - Perfil y Logout
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Divider()

                // Opción de Perfil
                SidebarButton(
                    text = "Mi Perfil",
                    onClick = onProfile,
                    icon = Icons.Default.Person
                )
            }
        }
    }
}

@Composable
private fun SidebarButton(
    text: String,
    onClick: () -> Unit,
    primary: Boolean = false,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    if (primary) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, fontWeight = FontWeight.SemiBold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text)
        }
    }
}