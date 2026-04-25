package com.example.mercader.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

/**
 * Par etiqueta + valor para fichas de detalle.
 *
 * Útil para mostrar atributos como Dificultad, Editorial etc.
 * en cualquier pantalla de detalle.
 *
 * @param label   Texto descriptivo pequeño (ej: "Dificultad")
 * @param value   Valor principal a mostrar (ej: "Media")
 * @param modifier Modifier externo
 */
@Composable
fun LabeledField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}