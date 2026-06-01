package com.example.mercader.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AddReviewModal(
    gameTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (rating: Int, content: String) -> Unit
) {
    var reviewContent by remember { mutableStateOf("") }
    var selectedRating by remember { mutableStateOf(0) }  // ← NUEVO: 0 = sin seleccionar
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val minChars = 10
    val maxChars = 500
    val currentChars = reviewContent.length
    val isRatingValid = selectedRating in 1..5
    val isContentValid = currentChars in minChars..maxChars
    val isFormValid = isRatingValid && isContentValid

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
                    text = "Escribir Reseña",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Nombre del juego
                Text(
                    text = gameTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Selector de estrellas (NUEVO)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tu calificación",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            IconButton(
                                onClick = { selectedRating = star },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (star <= selectedRating) Icons.Default.Star else Icons.Outlined.Star,
                                    contentDescription = "${star} estrella${if (star > 1) "s" else ""}",
                                    tint = if (star <= selectedRating)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    if (!isRatingValid && selectedRating > 0) {
                        Text(
                            text = "Selecciona entre 1 y 5 estrellas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de texto de la reseña
                OutlinedTextField(
                    value = reviewContent,
                    onValueChange = {
                        reviewContent = it
                        isError = false
                    },
                    label = { Text("Tu reseña") },
                    placeholder = { Text("Cuéntanos qué te pareció este juego...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6,
                    isError = isError,
                    supportingText = {
                        Text(
                            text = if (isError) errorMessage else "$currentChars / $maxChars caracteres",
                            color = if (currentChars > maxChars) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

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
                            when {
                                !isRatingValid -> {
                                    isError = true
                                    errorMessage = "Por favor, selecciona una calificación de 1 a 5 estrellas"
                                }
                                reviewContent.length < minChars -> {
                                    isError = true
                                    errorMessage = "La reseña debe tener al menos $minChars caracteres"
                                }
                                reviewContent.length > maxChars -> {
                                    isError = true
                                    errorMessage = "La reseña no puede exceder los $maxChars caracteres"
                                }
                                else -> {
                                    onConfirm(selectedRating, reviewContent.trim())
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = reviewContent.trim().isNotEmpty()
                    ) {
                        Text("Publicar")
                    }
                }
            }
        }
    }
}