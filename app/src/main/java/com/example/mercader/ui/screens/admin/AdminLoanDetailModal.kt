package com.example.mercader.ui.screens.admin

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
import com.example.mercader.domain.models.UserLoan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AdminLoanDetailModal(
    loan: UserLoan,
    onDismiss: () -> Unit,
    onConfirmPickup: (fechaInicio: String) -> Unit,
    onConfirmReturn: (fechaFin: String) -> Unit
) {
    var showPickupConfirm by remember { mutableStateOf(false) }
    var showReturnConfirm by remember { mutableStateOf(false) }

    val currentDateTime = getCurrentBoliviaDateTime()

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Detalle del Préstamo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                Text(
                    text = loan.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (loan.service == "prestamo") "📖 Préstamo (3 horas)" else "🎮 Alquiler (24 horas)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // DetailRow(label = "ID Préstamo", value = loan.loanId.take(15) + "...")
                DetailRow(label = "Fecha solicitud", value = formatDate(loan.requestDate))
                DetailRow(label = "Fecha límite", value = formatDate(loan.limitDate))

                if (loan.startDate != null) {
                    DetailRow(label = "Fecha recogida", value = formatDate(loan.startDate))
                }
                if (loan.endDate != null) {
                    DetailRow(label = "Fecha devolución", value = formatDate(loan.endDate))
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    loan.startDate == null -> {
                        Button(
                            onClick = { showPickupConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📦 Registrar Recogida")
                        }
                    }
                    loan.endDate == null -> {
                        Button(
                            onClick = { showReturnConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text("🔄 Registrar Devolución")
                        }
                    }
                    else -> {
                        Text(
                            text = "✓ Préstamo completado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }

    if (showPickupConfirm) {
        ConfirmActionModal(
            title = "Registrar Recogida",
            message = "¿Confirmar que el usuario ha recogido el juego \"${loan.title}\"?",
            onConfirm = {
                onConfirmPickup(currentDateTime)
                showPickupConfirm = false
            },
            onDismiss = { showPickupConfirm = false }
        )
    }

    if (showReturnConfirm) {
        ConfirmActionModal(
            title = "Registrar Devolución",
            message = "¿Confirmar que el usuario ha devuelto el juego \"${loan.title}\"?",
            onConfirm = {
                onConfirmReturn(currentDateTime)
                showReturnConfirm = false
            },
            onDismiss = { showReturnConfirm = false }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun getCurrentBoliviaDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val calendar = Calendar.getInstance().apply {
        // Ajustar a Bolivia (UTC-4)
        add(Calendar.HOUR_OF_DAY, -4)
    }
    return dateFormat.format(calendar.time)
}

@Composable
private fun ConfirmActionModal(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(onClick = onConfirm, modifier = Modifier.weight(1f)) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}