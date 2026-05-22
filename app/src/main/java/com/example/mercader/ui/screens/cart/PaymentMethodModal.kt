package com.example.mercader.ui.screens.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PaymentMethodModal(
    totalPrice: Double,
    onDismiss: () -> Unit,
    onSelectCardPayment: () -> Unit,
    onSelectQrPayment: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CARD) }

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
                    text = "Seleccionar Método de Pago",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Total a pagar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total a pagar:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Bs${String.format("%.2f", totalPrice)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle de selección
                Text(
                    text = "Elige tu método de pago",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Opciones de pago
                PaymentMethodCard(
                    title = "Tarjeta de Débito",
                    description = "Paga con tarjeta de débito o crédito",
                    icon = "💳",
                    isSelected = selectedMethod == PaymentMethod.CARD,
                    onClick = { selectedMethod = PaymentMethod.CARD }
                )

                PaymentMethodCard(
                    title = "Pago QR",
                    description = "Escanea el código QR para pagar",
                    icon = "📱",
                    isSelected = selectedMethod == PaymentMethod.QR,
                    onClick = { selectedMethod = PaymentMethod.QR }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de acción
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
                            when (selectedMethod) {
                                PaymentMethod.CARD -> onSelectCardPayment()
                                PaymentMethod.QR -> onSelectQrPayment()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Continuar")
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    title: String,
    description: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono
            Text(
                text = icon,
                fontSize = 32.sp
            )

            // Textos
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Radio button
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

enum class PaymentMethod {
    CARD, QR
}