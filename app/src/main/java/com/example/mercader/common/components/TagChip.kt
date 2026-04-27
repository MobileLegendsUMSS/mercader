package com.example.mercader.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Chip de etiqueta para categorias, etc.
 *
 * Soporta color de fondo y texto personalizados para adaptarse
 * a distintos contextos (categorías, géneros, estados, etc.).
 *
 * @param label           Texto de la etiqueta
 * @param modifier        Modifier externo
 * @param containerColor  Color de fondo del chip (por defecto: primaryContainer)
 * @param contentColor    Color del texto (por defecto: onPrimaryContainer)
 * @param cornerRadius    Radio de las esquinas redondeadas
 */
@Composable
fun TagChip(
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    cornerRadius: Dp = 8.dp
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(cornerRadius),
        modifier = modifier
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}