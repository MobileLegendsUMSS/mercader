package com.example.mercader.common.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun InProgressModal(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "En preoceso... ")
        },
        text = {
            Text(text = "Esta funcionalidad esta en desarrollo y estara disponible proximamente.")
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}