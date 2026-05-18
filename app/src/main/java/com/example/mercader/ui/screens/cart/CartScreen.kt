package com.example.mercader.ui.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercader.common.components.BackButton
import com.example.mercader.common.components.ImagePlaceholder
import com.example.mercader.common.utils.CartManager
import com.example.mercader.domain.models.CartItem

@Composable
fun CartScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val cartManager = remember { CartManager.getInstance(context) }
    var cartItems by remember { mutableStateOf(cartManager.getCart()) }

    // Función para refrescar el carrito
    fun refreshCart() {
        cartItems = cartManager.getCart()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onClick = onBack)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Mi Carrito",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.weight(1f))
            // Mostrar cantidad total de items
            if (cartItems.isNotEmpty()) {
                Text(
                    text = "${cartManager.getTotalItemCount()} items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Lista de juegos
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🛒",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onQuantityChange = {
                            refreshCart()
                        },
                        onRemove = {
                            cartManager.removeFromCart(cartItem.game.id)
                            refreshCart()
                        }
                    )
                }
            }

            // Total y botón de compra
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Bs${String.format("%.2f", cartManager.getTotalPrice())}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Envío:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Por calcular",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bs${String.format("%.2f", cartManager.getTotalPrice())}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Procesar compra */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Proceder al pago")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: () -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    val cartManager = remember { CartManager.getInstance(context) }
    val game = cartItem.game
    var quantity by remember { mutableStateOf(cartItem.quantity) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Imagen y detalles
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Placeholder de imagen
                ImagePlaceholder(
                    emoji = "🎮",
                    contentDescription = game.title,
                    modifier = Modifier.size(60.dp)
                )

                // Detalles del juego
                Column {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Precio unitario: Bs${String.format("%.2f", game.price)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Categoría: ${game.category.descripcion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Controles de cantidad y eliminar
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Controles +/-
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (quantity > 1) {
                                quantity--
                                cartManager.updateQuantity(game.id, quantity)
                                onQuantityChange()
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text(
                            text = "-",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.width(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    IconButton(
                        onClick = {
                            quantity++
                            cartManager.updateQuantity(game.id, quantity)
                            onQuantityChange()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Subtotal del item
                Text(
                    text = "Subtotal: Bs${String.format("%.2f", game.price * quantity)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Botón eliminar
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}