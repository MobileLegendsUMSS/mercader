package com.example.mercader.ui.screens.reports

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun CategoriasUsosScreen(viewModel: ReportViewModel) {

    // ── Colectar estados como State — Compose se resuscribe automáticamente ──
    val usosState       by viewModel.usosPeriodoState.collectAsState()
    val categoriasState by viewModel.categoriasPopularesState.collectAsState()

    var selectedPeriodo by remember { mutableStateOf("mes") }
    var selectedYear    by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR).toString()) }
    var selectedMonth   by remember { mutableStateOf(String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1)) }
    var showYearDialog  by remember { mutableStateOf(false) }
    var showMonthDialog by remember { mutableStateOf(false) }

    fun reload(periodo: String = selectedPeriodo, year: String = selectedYear, month: String = selectedMonth) {
        when (periodo) {
            "mes"  -> viewModel.loadUsosPeriodo("mes",  "$year-$month")
            "anio" -> viewModel.loadUsosPeriodo("anio", year)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadCategoriasPopulares()
        reload()
    }

    // ── Dialogs ─────────────────────────────────────────────────────────────
    if (showYearDialog) {
        AlertDialog(
            onDismissRequest = { showYearDialog = false },
            title = { Text("Seleccionar año", fontWeight = FontWeight.SemiBold) },
            text = {
                Column {
                    (2020..Calendar.getInstance().get(Calendar.YEAR)).reversed().forEach { year ->
                        TextButton(
                            onClick = {
                                selectedYear = year.toString()
                                showYearDialog = false
                                reload(year = year.toString())
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(year.toString()) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showYearDialog = false }) { Text("Cancelar") } }
        )
    }

    if (showMonthDialog && selectedPeriodo == "mes") {
        val months = linkedMapOf(
            "01" to "Enero",    "02" to "Febrero", "03" to "Marzo",
            "04" to "Abril",    "05" to "Mayo",    "06" to "Junio",
            "07" to "Julio",    "08" to "Agosto",  "09" to "Septiembre",
            "10" to "Octubre",  "11" to "Noviembre","12" to "Diciembre"
        )
        AlertDialog(
            onDismissRequest = { showMonthDialog = false },
            title = { Text("Seleccionar mes", fontWeight = FontWeight.SemiBold) },
            text = {
                Column {
                    months.forEach { (code, name) ->
                        TextButton(
                            onClick = {
                                selectedMonth = code
                                showMonthDialog = false
                                reload(month = code)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(name) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showMonthDialog = false }) { Text("Cancelar") } }
        )
    }

    // ── UI ───────────────────────────────────────────────────────────────────
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Préstamos y Alquileres (Endpoint 46)
        item {
            ReportCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Préstamos y Alquileres",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("Actividad por período",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(16.dp))

                    SegmentedPeriodoSelector(
                        selected = selectedPeriodo,
                        onSelect = { nuevo ->
                            if (nuevo != selectedPeriodo) {
                                selectedPeriodo = nuevo
                                reload(periodo = nuevo)
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showYearDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text(selectedYear, fontWeight = FontWeight.Medium) }

                        if (selectedPeriodo == "mes") {
                            val monthNames = linkedMapOf(
                                "01" to "Enero",    "02" to "Febrero", "03" to "Marzo",
                                "04" to "Abril",    "05" to "Mayo",    "06" to "Junio",
                                "07" to "Julio",    "08" to "Agosto",  "09" to "Septiembre",
                                "10" to "Octubre",  "11" to "Noviembre","12" to "Diciembre"
                            )
                            OutlinedButton(
                                onClick = { showMonthDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text(monthNames[selectedMonth] ?: selectedMonth, fontWeight = FontWeight.Medium) }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    AnimatedContent(
                        targetState = usosState,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "usos"
                    ) { state ->
                        when (state) {
                            is UsosPeriodoState.Loading -> ReportLoading()
                            is UsosPeriodoState.Success -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    UsoStatCard(Modifier.weight(1f), "Préstamos",
                                        state.usos.prestamos.toString(),
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.onPrimaryContainer)
                                    UsoStatCard(Modifier.weight(1f), "Alquileres",
                                        state.usos.alquileres.toString(),
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        MaterialTheme.colorScheme.onSecondaryContainer)
                                    UsoStatCard(Modifier.weight(1f), "Total",
                                        state.usos.prestamos_totales.toString(),
                                        MaterialTheme.colorScheme.tertiaryContainer,
                                        MaterialTheme.colorScheme.onTertiaryContainer)
                                }
                            }
                            is UsosPeriodoState.Empty -> ReportEmpty(state.message)
                            is UsosPeriodoState.Error -> ReportError(state.message)
                        }
                    }
                }
            }
        }

        // Popularidad de Categorías (Endpoint 44)
        item {
            ReportCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Popularidad de Categorías",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("Basado en frecuencia de préstamos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(16.dp))

                    when (val state = categoriasState) {
                        is CategoriasPopularesState.Loading -> ReportLoading()
                        is CategoriasPopularesState.Success -> {
                            state.categorias.forEachIndexed { index, categoria ->
                                CategoriaPopularItem(
                                    descripcion = categoria.descripcion,
                                    frecuencia = categoria.frecuencia,
                                    rank = index + 1
                                )
                                if (index < state.categorias.size - 1)
                                    Spacer(Modifier.height(14.dp))
                            }
                        }
                        is CategoriasPopularesState.Empty -> ReportEmpty(state.message)
                        is CategoriasPopularesState.Error -> ReportError(state.message)
                    }
                }
            }
        }
    }
}

// ── Componentes compartidos (usados por ambas screens) ──────────────────────

@Composable
fun ReportCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content = { content() }
    )
}

@Composable
fun SegmentedPeriodoSelector(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf("mes" to "Mensual", "anio" to "Anual").forEach { (key, label) ->
            Surface(
                onClick = { onSelect(key) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = if (selected == key) MaterialTheme.colorScheme.surface
                else Color.Transparent,
                tonalElevation = if (selected == key) 2.dp else 0.dp
            ) {
                Box(Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                    Text(label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected == key) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected == key) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ReportLoading() {
    Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}

@Composable
fun ReportEmpty(message: String) {
    Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ReportError(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer)
    }
}

@Composable
fun UsoStatCard(modifier: Modifier, label: String, value: String,
                containerColor: Color, contentColor: Color) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = containerColor) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = contentColor)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.75f))
        }
    }
}

@Composable
fun CategoriaPopularItem(descripcion: String, frecuencia: Double, rank: Int) {
    val progress = (frecuencia / 100f).toFloat().coerceIn(0f, 1f)
    val rankColor = when (rank) {
        1    -> Color(0xFFFFB300)
        2    -> Color(0xFF90A4AE)
        3    -> Color(0xFFBF8970)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(6.dp),
                    color = rankColor.copy(alpha = 0.15f)) {
                    Text("#$rank",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold, color = rankColor)
                }
                Text(descripcion, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
            }
            Text(String.format("%.1f%%", frecuencia),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant)
    }
}