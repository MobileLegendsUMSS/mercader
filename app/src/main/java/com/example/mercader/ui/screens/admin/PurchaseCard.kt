package com.example.mercader.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercader.data.remote.models.AdminPurchaseItemDTO

@Composable
fun PurchaseCard(
    purchase: AdminPurchaseItemDTO,
    onClick: () -> Unit
) {
    val shortId = if (purchase.idCompra.length >= 4) {
        purchase.idCompra.takeLast(4)
    } else {
        purchase.idCompra
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono segun estado
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when (purchase.estado) {
                            "pendiente" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            "aceptado" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (purchase.estado) {
                        "pendiente" -> "⏳"
                        "aceptado" -> "✅"
                        else -> "❌"
                    },
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Compra #$shortId",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total: Bs${String.format("%.2f", purchase.total)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Método: ${purchase.metodoPago}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${purchase.detallesCarrito.size} artículo(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Badge de estado
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (purchase.estado) {
                    "pendiente" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    "aceptado" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                }
            ) {
                Text(
                    text = when (purchase.estado) {
                        "pendiente" -> "Pendiente"
                        "aceptado" -> "Aceptada"
                        else -> "Rechazada"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = when (purchase.estado) {
                        "pendiente" -> MaterialTheme.colorScheme.primary
                        "aceptado" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}