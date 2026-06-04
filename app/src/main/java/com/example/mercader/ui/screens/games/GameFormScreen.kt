package com.example.mercader.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mercader.common.components.*
import com.example.mercader.common.constants.SliderType
import com.example.mercader.domain.models.Game
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon

@Composable
fun GameFormScreen(
    viewModel: GameFormViewModel = viewModel(),
    onEventSaved: () -> Unit = {},
    gameToEdit: Game? = null,
    onClose: () -> Unit = {}
) {
    val buttonText = if (gameToEdit != null) "Actualizar Juego" else "Guardar Juego"
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onEventSaved()
            viewModel.resetSuccess()
        }
    }

    LaunchedEffect(gameToEdit) {
        if (gameToEdit != null) {
            viewModel.setGameToEdit(gameToEdit)
        } else {
            viewModel.resetForm()
        }
    }

    state.errorMessage?.let { error ->
        LaunchedEffect(error) {
        }
    }

    FormContainer(title = "Formulario de Juego") {
        Box(modifier = Modifier.fillMaxWidth()) {

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionTitle("Información del juego")

                ThinTextField(
                    value = state.title,
                    onValueChange = { viewModel.updateGameTitle(it) },
                    label = "Nombre del Juego",
                )

                ThickTextField(
                    value = state.description,
                    onValueChange = { viewModel.updateGameDescription(it) },
                    label = "Descripcion del Juego",
                    minLines = 3
                )

                ThinTextField(
                    value = state.tutorial,
                    onValueChange = { viewModel.updateTutorial(it) },
                    label = "Enlace de Tutorial",
                )
                CustomSelector(
                    value = state.category,
                    onValueChange = { viewModel.updateCategory(it) },
                    label = "Categoria del juego",
                    options = state.gameCategories,
                    itemToString = { it.descripcion },
                    enabled = true
                )


                EnumSlider(
                    sliderType = SliderType.PEOPLE_COUNT,
                    value = state.nMinPerson.toFloat(),
                    onValueChange = { viewModel.updateMinPerson(it) }
                )

                EnumSlider(
                    sliderType = SliderType.PEOPLE_COUNT,
                    value = state.nMaxPerson.toFloat(),
                    onValueChange = { viewModel.updateMaxPerson(it) }
                )

                EnumSlider(
                    sliderType = SliderType.DURATION_HOURS,
                    value = state.minMinutes.toFloat(),
                    onValueChange = { viewModel.updateMinTime(it) }
                )

                EnumSlider(
                    sliderType = SliderType.DURATION_HOURS,
                    value = state.maxMinutes.toFloat(),
                    onValueChange = { viewModel.updateMaxTime(it) }
                )

                CustomSelector(
                    value = state.difficulty,
                    onValueChange = { viewModel.updateDifficulty(it) },
                    label = "Dificultad del juego",
                    options = state.difficulties,
                    itemToString = { it.descripcion },
                    enabled = true
                )

                CustomSelector(
                    value = state.editorial,
                    onValueChange = { viewModel.updateEditorial(it) },
                    label = "Editorial del juego",
                    options = state.editorials,
                    itemToString = { it.nombre },
                    enabled = true
                )

                NumberTextField(
                    value = state.stock.toString(),
                    onValueChange = { viewModel.updateStock(it) },
                    label = "Stock",
                )

                NumberTextField(
                    value = state.price.toString(),
                    onValueChange = { viewModel.updatePrice(it) },
                    label = "Precio",
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tipos de disponibilidad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = state.isPurchaseAvailable,
                                onCheckedChange = { viewModel.updatePurchaseAvailable(it) }
                            )
                            Text(
                                text = "Compra",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = state.isRentAvailable,
                                onCheckedChange = { viewModel.updateRentAvailable(it) }
                            )
                            Text(
                                text = "Alquiler",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.isLoanAvailable,
                            onCheckedChange = { viewModel.updateLoanAvailable(it) }
                        )
                        Text(
                            text = "Préstamo",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ===== BOTONES =====
                PrimaryButton(
                    text = buttonText,
                    onClick = { viewModel.saveGame() },
                    isLoading = state.isSaving,
                    enabled = state.title.isNotBlank()
                )
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