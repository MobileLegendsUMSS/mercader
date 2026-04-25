package com.example.mercader.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Chip compacto para mostrar un valor con un emoji decorativo.
 *
 * Uso en: jugadores, tiempo, etc.
 *
 * @param value      Texto principal a mostrar (ej: "2 - 4", "30 - 60 min")
 * @param emoji      Emoji decorativo a la izquierda del valor
 * @param modifier   Modifier externo (ej: Modifier.weight(1f))
 * @param cornerRadius Radio de las esquinas redondeadas
 */
@Composable
fun InfoChip(
    value: String,
    emoji: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(cornerRadius),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}