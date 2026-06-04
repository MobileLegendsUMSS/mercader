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
import com.example.mercader.domain.models.UserLoan

@Composable
fun AdminLoanCard(
    loan: UserLoan,
    onClick: () -> Unit
) {
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
            // Icono según tipo de servicio
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (loan.service == "prestamo")
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (loan.service == "prestamo") "📖" else "🎮",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = loan.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                //  Text(
                //      text = "ID: ${loan.loanId.take(10)}...",
                //      style = MaterialTheme.typography.bodySmall,
                //      color = MaterialTheme.colorScheme.onSurfaceVariant
                //  )
                Text(
                    text = "Tipo: ${if (loan.service == "prestamo") "Préstamo (3h)" else "Alquiler (24h)"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Fecha límite: ${formatDate(loan.limitDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Badge de estado
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when {
                    loan.endDate != null -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    loan.startDate != null -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                }
            ) {
                Text(
                    text = when {
                        loan.endDate != null -> "Devuelto"
                        loan.startDate != null -> "Recogido"
                        else -> "Pendiente"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        loan.endDate != null -> MaterialTheme.colorScheme.error
                        loan.startDate != null -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}