package com.example.mercader.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeleteGameModal(
    gameName: String,
    onConfirm: (justificacionRetiro: String) -> Unit,
    onDismiss: () -> Unit
) {
    var justificacionRetiro by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Retirar Juego") },
        text = {
            Column {
                Text("¿Esta seguro de dar de baja a: $gameName?")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = justificacionRetiro,
                    onValueChange = { justificacionRetiro = it },
                    label = { Text("Justificacion de Retiro") },
                    placeholder = { Text("Justificacion de Retiro") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(justificacionRetiro) },
                enabled = justificacionRetiro.isNotBlank()
            ) {
                Text("Confirmar Retiro")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}