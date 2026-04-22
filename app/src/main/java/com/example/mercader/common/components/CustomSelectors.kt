package com.example.mercader.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomSelector(
    value: T?,
    onValueChange: (T) -> Unit,
    label: String,
    options: List<T>,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    itemToString: (T) -> String = { it.toString() },
    placeholder: String = "Seleccionar..."
) {
    println("CustomSelector - Label: $label, Enabled: $enabled, Options count: ${options.size}, Value: $value")

    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value?.let { itemToString(it) } ?: "",
            onValueChange = {},
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clickable(enabled = enabled) {
                    println("CustomSelector clicked - Label: $label, ShowDialog: true")
                    showDialog = true
                },
            readOnly = true,
            enabled = enabled,
            isError = isError,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Desplegar opciones"
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (isError && errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }
    }

    if (showDialog) {
        println("CustomSelector Dialog opened - Label: $label, Options: $options")
        AlertDialog(
            onDismissRequest = {
                println("CustomSelector Dialog dismissed - Label: $label")
                showDialog = false
            },
            title = { Text("Seleccionar $label") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(options) { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    println("CustomSelector option selected - Label: $label, Selected: ${itemToString(option)}")
                                    onValueChange(option)
                                    showDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = option == value,
                                onClick = {
                                    println("CustomSelector RadioButton clicked - Label: $label, Selected: ${itemToString(option)}")
                                    onValueChange(option)
                                    showDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = itemToString(option),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    println("CustomSelector Cancel button clicked - Label: $label")
                    showDialog = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun StringSelector(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    placeholder: String = "Seleccionar..."
) {
    println("StringSelector - Label: $label, Value: '$value', Options count: ${options.size}, Enabled: $enabled")
    CustomSelector(
        value = value.ifEmpty { null },
        onValueChange = onValueChange,
        label = label,
        options = options,
        modifier = modifier,
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled,
        placeholder = placeholder,
        itemToString = { it }
    )
}