package com.example.mercader.ui.screens.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercader.R
import com.example.mercader.common.components.PrimaryButton

@Composable
fun QrPaymentScreen(
    totalPrice: Double,
    onBack: () -> Unit,
    onConfirmPayment: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = "Pago QR",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(72.dp))
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

        Spacer(modifier = Modifier.height(32.dp))

        // Imagen QR
        Card(
            modifier = Modifier
                .size(250.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Colocar tu imagen QR en res/drawable/qr_placeholder.png
                Image(
                    painter = painterResource(id = R.drawable.qr_placeholder),
                    contentDescription = "Código QR para pago",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Escanea el código QR con tu aplicación bancaria",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón confirmar
        PrimaryButton(
            text = "Confirmar Pago",
            onClick = onConfirmPayment,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mensaje de prueba
        //Text(
        //    text = "📱 Demo: Pago simulado, no se procesa realmente",
        //    fontSize = 12.sp,
        //    color = MaterialTheme.colorScheme.onSurfaceVariant,
        //    modifier = Modifier.fillMaxWidth(),
        //    textAlign = androidx.compose.ui.text.style.TextAlign.Center
        //)
    }
}