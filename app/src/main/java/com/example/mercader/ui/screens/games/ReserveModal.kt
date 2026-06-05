package com.example.mercader.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ReserveModal(
    gameTitle: String,
    isRentAvailable: Boolean = false,
    isLoanAvailable: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (tipoServicio: String) -> Unit
) {
    var selectedService by remember {
        mutableStateOf(
            when {
                isLoanAvailable -> "prestamo"
                isRentAvailable -> "alquiler"
                else -> null
            }
        )
    }

    val availableServices = listOf(
        isLoanAvailable to "prestamo",
        isRentAvailable to "alquiler",
    ).filter { it.first }.map { it.second }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text = "Solicitar Servicio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Nombre del juego
                Text(
                    text = gameTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Opciones de servicio disponibles
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {

                        if (isRentAvailable) {
                            ServiceRadioOption(
                                selected = selectedService == "alquiler",
                                onClick = { selectedService = "alquiler" },
                                emoji = "🎮",
                                title = "Alquiler",
                                description = "24 horas de alquiler"
                            )
                        }

                        if (isLoanAvailable) {
                            if (isRentAvailable) HorizontalDivider()
                            ServiceRadioOption(
                                selected = selectedService == "prestamo",
                                onClick = { selectedService = "prestamo" },
                                emoji = "📖",
                                title = "Préstamo",
                                description = "3 horas de préstamo"
                            )
                        }
                    }
                }

                // Mensaje si no hay servicios disponibles
                if (availableServices.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "No hay servicios disponibles para este juego en este momento",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    // Info adicional solo si hay servicios disponibles
                    Text(
                        text = "El juego debe ser devuelto antes de la fecha límite",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            selectedService?.let { onConfirm(it) }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = selectedService != null && availableServices.isNotEmpty()
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceRadioOption(
    selected: Boolean,
    onClick: () -> Unit,
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "$emoji $title",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}