package com.example.mercader.common.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.domain.models.Game
import com.example.mercader.ui.screens.games.CollectionViewModel.DeleteViewModel

@Composable
fun GameDetailDialog(
    game: Game,
    onDismiss: () -> Unit,
    onReserve: () -> Unit = {},
    onBuy: () -> Unit = {},
    onRent: () -> Unit = {},
    onTutorial: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DeleteViewModel = hiltViewModel(),
) {
    var showModal by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false  // Esto permite que el dialogo ocupe mas ancho
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)  // Aumentado de 0.85f a 0.9f para más altura
                .padding(horizontal = 20.dp),  // Muy poco padding horizontal (solo 8dp)
            shape = RoundedCornerShape(24.dp),  // Bordes un poco más redondeados
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Box para el boton de cerrar ──────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)  // Reducido de 56dp a 48dp
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)  // Reducido de 8dp a 4dp
                    ) {
                        Text(
                            text = "✕",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ── Columna Principal ─────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)  // Reducido de 16dp a 12dp
                ) {
                    // Titulo
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))  // Reducido de 16dp a 12dp

                    // Imagen
                    ImagePlaceholder(
                        emoji = "🎮",
                        contentDescription = game.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)  // Reducido de 250dp a 200dp para aprovechar espacio
                    )

                    Spacer(modifier = Modifier.height(12.dp))  // Reducido de 16dp a 12dp

                    // Tutorial y Precio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),  // Reducido de 12dp a 10dp
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SecondaryButton(
                            text = "📹 Tutorial",
                            onClick = onTutorial,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)  // Reducido de 48dp a 44dp
                        )

                        PriceDisplay(
                            price = game.price,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))  // Reducido de 12dp a 10dp

                    // Jugadores y Tiempo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)  // Reducido de 12dp a 10dp
                    ) {
                        InfoChip(
                            value = "${game.nMinPerson} - ${game.nMaxPerson}",
                            emoji = "👥",
                            modifier = Modifier.weight(1f)
                        )

                        InfoChip(
                            value = "${game.minMinutes} - ${game.maxMinutes} min",
                            emoji = "⏱️",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))  // Reducido de 16dp a 12dp

                    // Categorías
                    Text(
                        text = "Categorías",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))  // Reducido de 8dp a 6dp

                    if (game.category.id == "") {
                        Text(
                            text = "Sin categorías",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {/*
                            game.category.forEach { category ->
                                TagChip(label = category)
                            }*/
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))  // Reducido de 16dp a 12dp

                    // Descripción
                    Text(
                        text = "Descripcion",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = game.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))  // Reducido de 16dp a 12dp

                    // Dificultad y Editorial
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)  // Reducido de 16dp a 12dp
                    ) {
                        LabeledField(
                            label = "Dificultad",
                            value = game.difficulty.descripcion,
                            modifier = Modifier.weight(1f)
                        )

                        LabeledField(
                            label = "Editorial",
                            value = game.editorial.nombre,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))  // Reducido de 16dp a 12dp
                }

                // ── Botones ──────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),  // Reducido padding
                    verticalArrangement = Arrangement.spacedBy(6.dp)  // Reducido de 8dp a 6dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)  // Reducido de 8dp a 6dp
                    ) {
                        /*SecondaryButton(
                            text = "Alquilar",
                            onClick = onRent,
                            modifier = Modifier.weight(1f)
                        )*/

                        SecondaryButton(
                            text="Retirar Juego",
                            onClick = { showModal = true },
                            modifier = Modifier.weight(1f)
                        )

                        if (showModal) {
                            DeleteGameModal(
                                gameName = "Catan",
                                onConfirm = { justificacionRetiro ->
                                    Log.d("TestScreen", "Justificacion: $justificacionRetiro")
                                    viewModel.deleteGame(
                                        id = "69e5a7951edc217a4d7ab772",
                                        justificacionRetiro = justificacionRetiro
                                    )
                                    showModal = false
                                },
                                onDismiss = { showModal = false }
                            )
                        }


                        SecondaryButton(
                            text = "Reservar",
                            onClick = onReserve,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    PrimaryButton(
                        text = "Comprar",
                        onClick = onBuy,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))  // Reducido de 8dp a 6dp
            }
        }
    }
}