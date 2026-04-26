package com.example.mercader.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Placeholder visual para imágenes aún no cargadas (ej: sin Coil integrado).
 *
 * Cuando tengamos imagenes, este componente puede reemplazarse
 * o usarse como fallback en caso de error de carga.
 *
 * @param emoji        Emoji central representativo del contenido
 * @param contentDescription Descripción accesible del área
 * @param modifier     Modifier externo (define tamaño desde fuera)
 * @param cornerRadius Radio de las esquinas redondeadas
 */
@Composable
fun ImagePlaceholder(
    emoji: String,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(cornerRadius)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }
    }
}