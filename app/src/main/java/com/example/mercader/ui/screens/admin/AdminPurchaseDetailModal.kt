package com.example.mercader.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mercader.common.utils.ReserveManager
import com.example.mercader.data.remote.models.AdminPurchaseItemDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPurchaseDetailModal(
    purchase: AdminPurchaseItemDTO,
    onDismiss: () -> Unit,
    onConfirmAccept: () -> Unit,
    onConfirmReject: () -> Unit,
    viewModel: AdminPurchaseDetailViewModel = hiltViewModel()
) {
    var showAcceptConfirm by remember { mutableStateOf(false) }
    var showRejectConfirm by remember { mutableStateOf(false) }
    val detailState by viewModel.state.collectAsState()

    LaunchedEffect(purchase.idCompra) {
        viewModel.loadPurchaseDetail(purchase.idCompra)
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
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)  // Altura máxima del modal
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ✅ Área scrolleable para el contenido
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Título
                    Text(
                        text = "Detalle de Compra",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider()

                    // Contenido que puede crecer
                    if (detailState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    } else if (detailState.purchaseDetail != null) {
                        val detail = detailState.purchaseDetail!!

                        Text(
                            text = "Compra #${purchase.idCompra.takeLast(4)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Estado: ${when (purchase.estado) {
                                "pendiente" -> "Pendiente"
                                "aceptado" -> "Aceptada"
                                else -> "Rechazada"
                            }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (purchase.estado) {
                                "pendiente" -> MaterialTheme.colorScheme.primary
                                "aceptado" -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.error
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow(label = "Usuario", value = detail.nombreUsuario)

                        if (detail.nombres != null || detail.apellidos != null) {
                            DetailRow(
                                label = "Nombre completo",
                                value = "${detail.nombres ?: ""} ${detail.apellidos ?: ""}".trim()
                            )
                        }

                        DetailRow(label = "Total", value = "Bs${String.format("%.2f", detail.total)}")
                        DetailRow(label = "Método de pago", value = purchase.metodoPago)
                        DetailRow(label = "Fecha", value = formatDate(detail.fechaCreacion))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Comprobante de pago
                        Text(
                            text = "Comprobante de pago",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            AsyncImage(
                                model = detail.comprobante,
                                contentDescription = "Comprobante de pago",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Detalle del carrito
                        Text(
                            text = "Productos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        detail.detallesCarrito.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.title} x${item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Bs${String.format("%.2f", item.priceSubtotal)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bs${String.format("%.2f", detail.total)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    if (purchase.estado == "pendiente") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showRejectConfirm = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Rechazar")
                            }

                            Button(
                                onClick = { showAcceptConfirm = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text("Aceptar")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }

    if (showAcceptConfirm) {
        ConfirmActionModal(
            title = "Aceptar Compra",
            message = "¿Confirmar que deseas ACEPTAR esta compra?",
            onConfirm = {
                showAcceptConfirm = false
                onConfirmAccept()
            },
            onDismiss = { showAcceptConfirm = false }
        )
    }

    if (showRejectConfirm) {
        ConfirmActionModal(
            title = "Rechazar Compra",
            message = "¿Confirmar que deseas RECHAZAR esta compra?",
            onConfirm = {
                showRejectConfirm = false
                onConfirmReject()
            },
            onDismiss = { showRejectConfirm = false }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ConfirmActionModal(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(onClick = onConfirm, modifier = Modifier.weight(1f)) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}