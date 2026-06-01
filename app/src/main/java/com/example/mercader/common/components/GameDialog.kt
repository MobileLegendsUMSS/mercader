package com.example.mercader.common.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.mercader.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.domain.models.Game
import com.example.mercader.ui.screens.games.CollectionViewModel.DeleteViewModel
import com.example.mercader.ui.screens.games.FavoriteViewModel
import com.example.mercader.common.utils.CartManager
import com.example.mercader.common.utils.ReserveManager
import kotlinx.coroutines.launch

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
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    cartManager: CartManager,
    reserveManager: ReserveManager,
    onEditGame: ((Game) -> Unit)? = null,
    onCartUpdate: (() -> Unit)? = null,
    onNavigateToCart: (() -> Unit)? = null,
    onNavigateToReviews: ((Game) -> Unit)? = null
) {
    var showModal by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isInCart by remember { mutableStateOf(false) }
    var cartQuantity by remember { mutableStateOf(0) }
    var isAddingToCart by remember { mutableStateOf(false) }
    var isLoadingCartState by remember { mutableStateOf(true) }
    var showReserveModal by remember { mutableStateOf(false) }
    var isProcessingReserve by remember { mutableStateOf(false) }

    val favoriteIds by favoriteViewModel.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(game.id)
    val isFavoriteLoading by favoriteViewModel.isLoading.collectAsState()

    // CoroutineScope para lanzar corrutinas
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(game.id) {
        Log.d("FAVORITE_DEBUG", "Game ID en Dialog: '${game.id}'")
        isLoadingCartState = true
        favoriteViewModel.loadFavorites()
        try {
            isInCart = cartManager.isInCart(game.id)
            if (isInCart) {
                cartQuantity = cartManager.getQuantity(game.id)
            }
        } catch (e: Exception) {
            Log.e("GameDetailDialog", "Error loading cart state: ${e.message}")
        } finally {
            isLoadingCartState = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Box para el boton de cerrar y favorito ──────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grupo izquierdo (corazón + comentario)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)  // Espacio entre los dos iconos
                    ) {
                        // Boton favorito (corazón)
                        IconButton(
                            onClick = {
                                favoriteViewModel.toggleFavorite(game.id) { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            enabled = !isFavoriteLoading
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Boton reseña
                        IconButton(
                            onClick = {
                                onNavigateToReviews?.invoke(game)
                                //Toast.makeText(context, "Reseñas", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.comment_icon),
                                contentDescription = "Reseñas",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Boton cerrar (derecha)
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
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
                        .padding(horizontal = 12.dp)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Imagen
                    ImagePlaceholder(
                        emoji = "🎮",
                        contentDescription = game.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tutorial y Precio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SecondaryButton(
                            text = "📹 Tutorial",
                            onClick = onTutorial,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        )

                        PriceDisplay(
                            price = game.price,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Jugadores y Tiempo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Categorías
                    Text(
                        text = "Categorías",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

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
                        ) {
                            TagChip(label = game.category.descripcion)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dificultad y Editorial
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ── Botones ──────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SecondaryButton(
                            text = "Retirar Juego",
                            onClick = { showModal = true },
                            modifier = Modifier.weight(1f)
                        )

                        if (showReserveModal) {
                            ReserveModal(
                                gameTitle = game.title,
                                onDismiss = { showReserveModal = false },
                                onConfirm = { tipoServicio ->
                                    showReserveModal = false
                                    isProcessingReserve = true
                                    coroutineScope.launch {
                                        try {
                                            val result = reserveManager.bookReserve(game.id, tipoServicio)
                                            if (result.isSuccess) {
                                                Toast.makeText(
                                                    context,
                                                    "✓ ${game.title} - ${if (tipoServicio == "prestamo") "Préstamo" else "Alquiler"} solicitado con éxito!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                                                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isProcessingReserve = false
                                        }
                                    }
                                }
                            )
                        }

                        if (showModal) {
                            DeleteGameModal(
                                gameName = game.title,
                                onConfirm = { justificacionRetiro ->
                                    Log.d("TestScreen", "Justificacion: $justificacionRetiro")
                                    viewModel.deleteGame(
                                        id = game.id,
                                        justificacionRetiro = justificacionRetiro
                                    )
                                    showModal = false
                                },
                                onDismiss = { showModal = false }
                            )
                        }

                        SecondaryButton(
                            text = "Editar Juego",
                            onClick = {
                                onEditGame?.invoke(game)
                            },
                            modifier = Modifier.weight(1f)
                        )

                        SecondaryButton(
                            text = "Solicitar Prestamo",
                            onClick = { showReserveModal = true },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Botón dinámico del carrito
                    Button(
                        onClick = {
                            if (isInCart) {
                                // Navegar al carrito
                                onNavigateToCart?.invoke()
                                onDismiss()
                            } else {
                                // Agregar al carrito con corrutina
                                isAddingToCart = true
                                coroutineScope.launch {
                                    try {
                                        val added = cartManager.addToCart(game)
                                        if (added) {
                                            // Actualizar estado local
                                            isInCart = true
                                            cartQuantity = cartManager.getQuantity(game.id)
                                            Toast.makeText(
                                                context,
                                                "✓ ${game.title} se ha añadido al carrito",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            onCartUpdate?.invoke()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error al añadir ${game.title} al carrito",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error de conexión: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } finally {
                                        isAddingToCart = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isAddingToCart && !isLoadingCartState,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInCart)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isAddingToCart) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = if (isInCart) "Ver Carrito ($cartQuantity)" else "Añadir al Carrito",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}