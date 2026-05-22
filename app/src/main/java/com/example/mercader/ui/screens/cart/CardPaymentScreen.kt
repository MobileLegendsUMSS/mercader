package com.example.mercader.ui.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercader.common.components.PrimaryButton
import com.example.mercader.common.components.ThinTextField

@Composable
fun CardPaymentScreen(
    totalPrice: Double,
    onBack: () -> Unit,
    onConfirmPayment: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Volver")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Pago con Tarjeta",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(72.dp)) // Balance
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Resumen del pago
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
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
                    text = "💰 Total a pagar",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Bs${String.format("%.2f", totalPrice)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Formulario de tarjeta
        Text(
            text = "Datos de la tarjeta",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThinTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it.take(19) },
            label = "Número de tarjeta",
            placeholder = "1234 5678 9012 3456"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThinTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it.take(5) },
                label = "Fecha expiración",
                placeholder = "MM/AA",
                modifier = Modifier.weight(1f)
            )

            ThinTextField(
                value = cvv,
                onValueChange = { cvv = it.take(4) },
                label = "CVV",
                placeholder = "123",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ThinTextField(
            value = cardHolderName,
            onValueChange = { cardHolderName = it },
            label = "Nombre del titular",
            placeholder = "Como aparece en la tarjeta"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón confirmar
        PrimaryButton(
            text = "Confirmar Pago",
            onClick = onConfirmPayment,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mensaje de prueba
        //Text(
        //text = "🔒 Demo: No se validan datos reales",
        //    fontSize = 12.sp,
        //    color = MaterialTheme.colorScheme.onSurfaceVariant,
        //    modifier = Modifier.fillMaxWidth(),
        //    textAlign = androidx.compose.ui.text.style.TextAlign.Center
        //)
    }
}