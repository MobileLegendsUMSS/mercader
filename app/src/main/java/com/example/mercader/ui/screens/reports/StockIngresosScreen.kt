package com.example.mercader.ui.screens.reports

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.*

@Composable
fun StockIngresosScreen(viewModel: ReportViewModel) {

    // ── Colectar estados como State — Compose se resuscribe automáticamente ──
    val ingresosState by viewModel.ingresosPeriodoState.collectAsState()
    val stockState    by viewModel.juegosStockState.collectAsState()

    var selectedPeriodo by remember { mutableStateOf("mes") }
    var selectedYear    by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR).toString()) }
    var selectedMonth   by remember { mutableStateOf(String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1)) }
    var showYearDialog  by remember { mutableStateOf(false) }
    var showMonthDialog by remember { mutableStateOf(false) }

    // Única función de carga — siempre dispara, el ViewModel cancela la anterior
    fun reload(periodo: String = selectedPeriodo, year: String = selectedYear, month: String = selectedMonth) {
        when (periodo) {
            "mes"  -> viewModel.loadIngresosPeriodo("mes",  "$year-$month")
            "anio" -> viewModel.loadIngresosPeriodo("anio", year)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadJuegosStock(allGames = true, order = "ascendente", amount = null)
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

        // Ingresos (Endpoint 45)
        item {
            ReportCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Ingresos por Período",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("Total de ventas registradas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(16.dp))

                    // Selector Mensual / Anual
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

                    // Año + Mes
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
                        targetState = ingresosState,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "ingresos"
                    ) { state ->
                        when (state) {
                            is IngresosPeriodoState.Loading -> ReportLoading()
                            is IngresosPeriodoState.Success -> {
                                val format = NumberFormat.getCurrencyInstance(Locale("es", "BO"))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Ingreso total",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                        Text(format.format(state.ganancia),
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                }
                            }
                            is IngresosPeriodoState.Empty -> ReportEmpty(state.message)
                            is IngresosPeriodoState.Error -> ReportError(state.message)
                        }
                    }
                }
            }
        }

        // Stock (Endpoint 43)
        item {
            ReportCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Juegos por Stock",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("Todos los juegos ordenados por cantidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(16.dp))

                    when (val state = stockState) {
                        is JuegosStockState.Loading -> ReportLoading()
                        is JuegosStockState.Success -> {
                            state.juegos.forEachIndexed { index, juego ->
                                StockItem(rank = index + 1, titulo = juego.titulo,
                                    stock = juego.cantidad, disponible = juego.disponible)
                                if (index < state.juegos.size - 1)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                        is JuegosStockState.Empty -> ReportEmpty(state.message)
                        is JuegosStockState.Error -> ReportError(state.message)
                    }
                }
            }
        }
    }
}

// ── Componentes ─────────────────────────────────────────────────────────────

@Composable
fun StockItem(rank: Int, titulo: String, stock: Int, disponible: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("$rank", style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(titulo, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Text(
                    if (disponible) "Disponible" else "No disponible",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (disponible) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error)
            }
        }
        Spacer(Modifier.width(8.dp))
        Surface(shape = RoundedCornerShape(8.dp),
            color = if (stock > 0) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer) {
            Text("$stock uds.", style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                color = if (stock > 0) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}