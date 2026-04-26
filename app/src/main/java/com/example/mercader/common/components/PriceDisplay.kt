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
 * Muestra un precio de forma destacada dentro de una Card.
 *
 * Se puede usar en diálogos, listas de productos, fichas de detalle, etc.
 *
 * @param price        Valor numérico del precio
 * @param currencySymbol Símbolo de moneda (por defecto "Bs")
 * @param modifier     Modifier externo (ej: Modifier.weight(1f))
 * @param height       Alto del componente
 * @param cornerRadius Radio de las esquinas redondeadas
 */
@Composable
fun PriceDisplay(
    price: Number,
    currencySymbol: String = "Bs",
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    cornerRadius: Dp = 8.dp
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$price$currencySymbol",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}