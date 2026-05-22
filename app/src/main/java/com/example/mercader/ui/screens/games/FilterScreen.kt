package com.example.mercader.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.common.components.*

@Composable
fun FilterScreen(
    viewModel: FilterViewModel = hiltViewModel(),
    onApplyFilters: () -> Unit = {},
    onClearFilters: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    FormContainer(title = "Filtrar Juegos") {

        Box(modifier = Modifier.fillMaxWidth()) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                state.errorMessage?.let { error ->
                    LaunchedEffect(error) {

                    }
                }

                if (state.isLoading) {

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                } else {

                    SectionTitle("Filtros disponibles")

                    CustomSelector(
                        value = state.selectedCategory,
                        onValueChange = { viewModel.updateCategory(it) },
                        label = "Categoría",
                        options = state.gameCategories,
                        itemToString = { it.descripcion },
                        enabled = true,
                        placeholder = "Todas las categorías"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SectionTitle("Número de jugadores")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Column(modifier = Modifier.weight(1f)) {

                            CustomSlider(
                                value = state.nMinPerson.toFloat(),
                                onValueChange = { viewModel.updateMinPerson(it) },
                                label = "Mínimo",
                                valueRange = 1f..8f,
                                steps = 7,
                                valueFormatter = { it.toInt().toString() }
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {

                            CustomSlider(
                                value = state.nMaxPerson.toFloat(),
                                onValueChange = { viewModel.updateMaxPerson(it) },
                                label = "Máximo",
                                valueRange = 1f..8f,
                                steps = 7,
                                valueFormatter = { it.toInt().toString() }
                            )
                        }
                    }

                    SectionTitle("Duración (minutos)")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Column(modifier = Modifier.weight(1f)) {

                            CustomSlider(
                                value = state.minMinutes.toFloat(),
                                onValueChange = { viewModel.updateMinTime(it) },
                                label = "Mínimo",
                                valueRange = 0f..240f,
                                steps = 24,
                                valueFormatter = { "${it.toInt()} min" }
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {

                            CustomSlider(
                                value = state.maxMinutes.toFloat(),
                                onValueChange = { viewModel.updateMaxTime(it) },
                                label = "Máximo",
                                valueRange = 0f..240f,
                                steps = 24,
                                valueFormatter = { "${it.toInt()} min" }
                            )
                        }
                    }

                    CustomSelector(
                        value = state.selectedDifficulty,
                        onValueChange = { viewModel.updateDifficulty(it) },
                        label = "Dificultad",
                        options = state.difficulties,
                        itemToString = { it.descripcion },
                        enabled = true,
                        placeholder = "Todas las dificultades"
                    )

                    CustomSelector(
                        value = state.selectedEditorial,
                        onValueChange = { viewModel.updateEditorial(it) },
                        label = "Editorial",
                        options = state.editorials,
                        itemToString = { it.nombre },
                        enabled = true,
                        placeholder = "Todas las editoriales"
                    )

                    SectionTitle("Rango de precio")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Column(modifier = Modifier.weight(1f)) {

                            NumberTextField(
                                value = if (state.minPrice == null) "" else state.minPrice.toString(),
                                onValueChange = { viewModel.updateMinPrice(it) },
                                label = "Precio mínimo",
                                placeholder = "0",
                                allowDecimals = true
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {

                            NumberTextField(
                                value = if (state.maxPrice == null) "" else state.maxPrice.toString(),
                                onValueChange = { viewModel.updateMaxPrice(it) },
                                label = "Precio máximo",
                                placeholder = "Sin límite",
                                allowDecimals = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.hasActiveFilters) {

                        AssistChip(
                            onClick = { viewModel.clearAllFilters() },
                            label = { Text("Filtros activos - Limpiar todo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        SecondaryButton(
                            text = "Limpiar filtros",
                            onClick = {
                                viewModel.clearAllFilters()
                                onClearFilters()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        PrimaryButton(
                            text = "Aplicar filtros",
                            onClick = {
                                onApplyFilters()
                            },
                            modifier = Modifier.weight(1f),
                            enabled = true
                        )
                    }
                }
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar"
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
    )
}