package com.example.mercader.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun DateTimePickerModal(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (dateTime: String) -> Unit
) {
    val context = LocalContext.current

    // Obtener fecha/hora actual de Bolivia (UTC-4)
    val currentBoliviaDateTime = getCurrentBoliviaDateTimeObject()

    var selectedDate by remember { mutableStateOf(currentBoliviaDateTime.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(currentBoliviaDateTime.toLocalTime()) }
    var showDatePicker by remember { mutableStateOf(true) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Validaciones
    val isDateValid = !selectedDate.isAfter(currentBoliviaDateTime.toLocalDate())
    val isTimeValid = if (selectedDate == currentBoliviaDateTime.toLocalDate()) {
        !selectedTime.isAfter(currentBoliviaDateTime.toLocalTime())
    } else {
        true
    }
    val isSelectionValid = isDateValid && isTimeValid

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
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Fecha seleccionada
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Fecha y hora seleccionada",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${formatDateForDisplay(selectedDate)} - ${formatTimeForDisplay(selectedTime)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Botones de selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cambiar fecha")
                    }
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cambiar hora")
                    }
                }

                // Mensajes de validación
                if (!isDateValid) {
                    Text(
                        text = "⚠️ La fecha no puede ser futura",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (!isTimeValid) {
                    Text(
                        text = "⚠️ La hora no puede ser futura",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de acción
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
                            val dateTime = combineDateAndTime(selectedDate, selectedTime)
                            onConfirm(dateTime)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = isSelectionValid
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }

    // DatePicker Modal
    if (showDatePicker) {
        DatePickerModal(
            initialDate = selectedDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            }
        )
    }

    // TimePicker Modal
    if (showTimePicker) {
        TimePickerModal(
            initialTime = selectedTime,
            selectedDate = selectedDate,
            currentDateTime = currentBoliviaDateTime,
            onDismiss = { showTimePicker = false },
            onTimeSelected = { time ->
                selectedTime = time
                showTimePicker = false
            }
        )
    }
}

@Composable
fun DatePickerModal(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val currentBoliviaDate = getCurrentBoliviaDateTimeObject().toLocalDate()
    var selectedDate by remember { mutableStateOf(initialDate) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Seleccionar fecha",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker simple usando calendario manual
                DatePickerGrid(
                    selectedDate = selectedDate,
                    currentDate = currentBoliviaDate,
                    onDateSelected = { date ->
                        if (!date.isAfter(currentBoliviaDate)) {
                            selectedDate = date
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { onDateSelected(selectedDate) },
                        modifier = Modifier.weight(1f),
                        enabled = !selectedDate.isAfter(currentBoliviaDate)
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerModal(
    initialTime: LocalTime,
    selectedDate: LocalDate,
    currentDateTime: LocalDateTime,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableStateOf(initialTime.minute) }

    val currentHour = currentDateTime.hour
    val currentMinute = currentDateTime.minute
    val isToday = selectedDate == currentDateTime.toLocalDate()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Seleccionar hora",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de hora simple
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selector de hora
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hora", style = MaterialTheme.typography.labelMedium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    if (selectedHour > 0) {
                                        val newHour = selectedHour - 1
                                        if (!isToday || newHour >= currentHour) {
                                            selectedHour = newHour
                                        }
                                    }
                                }
                            ) {
                                Text("▲", fontSize = 24.sp)
                            }
                            Text(
                                text = String.format("%02d", selectedHour),
                                style = MaterialTheme.typography.displayMedium,
                                modifier = Modifier.width(60.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(
                                onClick = {
                                    if (selectedHour < 23) {
                                        val newHour = selectedHour + 1
                                        if (!isToday || newHour <= 23) {
                                            selectedHour = newHour
                                        }
                                    }
                                }
                            ) {
                                Text("▼", fontSize = 24.sp)
                            }
                        }
                    }

                    Text(":", fontSize = 32.sp, modifier = Modifier.padding(horizontal = 16.dp))

                    // Selector de minutos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Minuto", style = MaterialTheme.typography.labelMedium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    if (selectedMinute > 0) {
                                        val newMinute = selectedMinute - 1
                                        if (!isToday || selectedHour > currentHour || newMinute >= currentMinute) {
                                            selectedMinute = newMinute
                                        }
                                    }
                                }
                            ) {
                                Text("▲", fontSize = 24.sp)
                            }
                            Text(
                                text = String.format("%02d", selectedMinute),
                                style = MaterialTheme.typography.displayMedium,
                                modifier = Modifier.width(60.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(
                                onClick = {
                                    if (selectedMinute < 59) {
                                        selectedMinute++
                                    }
                                }
                            ) {
                                Text("▼", fontSize = 24.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            onTimeSelected(LocalTime.of(selectedHour, selectedMinute))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerGrid(
    selectedDate: LocalDate,
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Implementación simple de grid de fechas
    val daysInMonth = selectedDate.lengthOfMonth()
    val firstDayOfMonth = selectedDate.withDayOfMonth(1).dayOfWeek.value

    Column {
        // Header con mes y año
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(
                    onClick = {
                        val newDate = selectedDate.minusMonths(1)
                        if (!newDate.isAfter(currentDate)) {
                            onDateSelected(newDate)
                        }
                    }
                ) {
                    Text("◀")
                }
                IconButton(
                    onClick = {
                        val newDate = selectedDate.plusMonths(1)
                        if (!newDate.isAfter(currentDate)) {
                            onDateSelected(newDate)
                        }
                    },
                    enabled = !selectedDate.plusMonths(1).isAfter(currentDate)
                ) {
                    Text("▶")
                }
            }
        }

        // Días de la semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Grid de días
        val calendar = Calendar.getInstance()
        calendar.set(selectedDate.year, selectedDate.monthValue - 1, 1)
        val startOffset = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7

        Column {
            for (week in 0..5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (dayInWeek in 0..6) {
                        val dayNumber = week * 7 + dayInWeek - startOffset + 1
                        val date = if (dayNumber in 1..daysInMonth) {
                            LocalDate.of(selectedDate.year, selectedDate.monthValue, dayNumber)
                        } else null

                        val isSelectable = date != null && !date.isAfter(currentDate)
                        val isSelected = date == selectedDate && isSelectable

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .size(40.dp)  // Tamaño fijo cuadrado
                                .clickable(enabled = isSelectable) {
                                    if (isSelectable) onDateSelected(date)
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = if (isSelected) 0.dp else 1.dp
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (date != null) dayNumber.toString() else "",
                                    fontSize = 14.sp,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Funciones auxiliares
private fun getCurrentBoliviaDateTimeObject(): LocalDateTime {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, -4)
    }
    return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault())
}

private fun combineDateAndTime(date: LocalDate, time: LocalTime): String {
    val dateTime = LocalDateTime.of(date, time)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    return dateTime.format(formatter)
}

private fun formatDateForDisplay(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return date.format(formatter)
}

private fun formatTimeForDisplay(time: LocalTime): String {
    return String.format("%02d:%02d", time.hour, time.minute)
}