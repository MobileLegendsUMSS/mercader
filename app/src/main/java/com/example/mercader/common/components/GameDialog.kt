package com.example.mercader.common.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.domain.models.Game
import com.example.mercader.ui.screens.games.CollectionViewModel.DeleteViewModel
import com.example.mercader.ui.screens.games.FavoriteViewModel
import com.example.mercader.common.utils.CartManager
import com.example.mercader.common.utils.ReserveManager
import com.example.mercader.ui.screens.games.ReserveModal
import kotlinx.coroutines.launch

@Composable
fun GameDetailDialog(
    game: Game,
    onDismiss: () -> Unit,
    onTutorial: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DeleteViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    cartManager: CartManager? = null,  // ? Nullable para modo admin
    reserveManager: ReserveManager? = null,  // ? Nullable para modo admin
    onEditGame: ((Game) -> Unit)? = null,
    onDeleteGame: ((Game) -> Unit)? = null,  // ? Callback para eliminar en modo admin
    onCartUpdate: (() -> Unit)? = null,
    onNavigateToCart: (() -> Unit)? = null,
    onNavigateToReviews: ((Game) -> Unit)? = null,
    isAdminMode: Boolean = false  // ? Modo admin
) {
    var showModal by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }  // ? Confirmación de eliminación
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

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(game.id) {
        Log.d("FAVORITE_DEBUG", "Game ID en Dialog: '${game.id}'")
        if (!isAdminMode && cartManager != null) {
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
        } else {
            isLoadingCartState = false
            favoriteViewModel.loadFavorites()
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

                // ? Header con botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grupo izquierdo - Solo visible en modo usuario
                    if (!isAdminMode) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Botón favorito
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

                            // Botón reseña
                            IconButton(
                                onClick = {
                                    onNavigateToReviews?.invoke(game)
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
                    } else {
                        // Indicador modo admin
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "\uD83D\uDC51 Modo Admin",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Botón cerrar
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ? Columna Principal
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)
                ) {
                    // Título
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
                    if (!game.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = game.imageUrl,
                            contentDescription = game.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        ImagePlaceholder(
                            emoji = "?",
                            contentDescription = game.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tutorial y Precio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SecondaryButton(
                            text = "\uD83D\uDD17 Tutorial",
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
                            emoji = "\uD83D\uDC64",
                            modifier = Modifier.weight(1f)
                        )

                        InfoChip(
                            value = "${game.minMinutes} - ${game.maxMinutes} min",
                            emoji = "⌛",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stock para modo admin (de betterStock)
                    if (isAdminMode) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "\uD83D\uDCE6 Stock disponible:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${game.stock} unidades",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (game.stock > 0)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

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
                        text = "Descripción",
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

                // ? Botones
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isAdminMode) {
                        // ? MODO ADMIN: Botones de Editar y Eliminar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SecondaryButton(
                                text = "✏\uFE0FEditar Juego",
                                onClick = {
                                    onEditGame?.invoke(game)
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = { showDeleteConfirmation = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("\uD83D\uDDD1\uFE0F Eliminar")
                            }
                        }
                    } else {
                        // ? MODO USUARIO: Botones de Retirar y Préstamo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SecondaryButton(
                                text = "Retirar Juego",
                                onClick = { showModal = true },
                                modifier = Modifier.weight(1f)
                            )

                            // Botón Solicitar Préstamo con validación de disponibilidad (de dev)
                            Button(
                                onClick = {
                                    val hasAnyService = game.isPurchaseAvailable ||
                                            game.isRentAvailable ||
                                            game.isLoanAvailable

                                    if (hasAnyService && reserveManager != null) {
                                        showReserveModal = true
                                    } else if (!hasAnyService) {
                                        Toast.makeText(
                                            context,
                                            "No hay servicios disponibles para este juego",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error: Sistema de reservas no disponible",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = reserveManager != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text("Solicitar Préstamo")
                            }
                        }

                        // Botón dinámico del carrito (con validación de isPurchaseAvailable de dev)
                        if (cartManager != null) {
                            Button(
                                onClick = {
                                    if (isInCart) {
                                        onNavigateToCart?.invoke()
                                        onDismiss()
                                    } else {
                                        isAddingToCart = true
                                        coroutineScope.launch {
                                            try {
                                                val added = cartManager.addToCart(game)
                                                if (added) {
                                                    isInCart = true
                                                    cartQuantity = cartManager.getQuantity(game.id)
                                                    Toast.makeText(
                                                        context,
                                                        "? ${game.title} se ha añadido al carrito",
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
                                enabled = !isAddingToCart && !isLoadingCartState && game.isPurchaseAvailable,  // ? De dev
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
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }

    // Modal para "Retirar Juego"
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
                onDismiss()  // Cerrar diálogo después de retirar
            },
            onDismiss = { showModal = false }
        )
    }

    // ? Modal de confirmación de eliminación (modo admin)
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Eliminar juego") },
            text = { Text("¿Estás seguro de que deseas eliminar \"${game.title}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteGame?.invoke(game)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ? Modal de reserva mejorado (de dev + betterStock)
    if (showReserveModal && reserveManager != null) {
        ReserveModal(
            gameTitle = game.title,
            isRentAvailable = game.isRentAvailable,  // ? De dev
            isLoanAvailable = game.isLoanAvailable,  // ? De dev
            onDismiss = { showReserveModal = false },
            onConfirm = { tipoServicio ->
                showReserveModal = false
                isProcessingReserve = true
                coroutineScope.launch {
                    try {
                        val result = reserveManager.bookReserve(game.id, tipoServicio)
                        if (result.isSuccess) {
                            val servicioTexto = when (tipoServicio) {
                                "compra" -> "Compra"
                                "alquiler" -> "Alquiler"
                                "prestamo" -> "Préstamo"
                                else -> "Servicio"
                            }
                            Toast.makeText(
                                context,
                                "? ${game.title} - $servicioTexto solicitado con éxito!",
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
}