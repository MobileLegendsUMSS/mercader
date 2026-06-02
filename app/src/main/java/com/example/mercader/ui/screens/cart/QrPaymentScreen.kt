package com.example.mercader.ui.screens.cart

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.mercader.R
import com.example.mercader.common.components.PrimaryButton
import java.io.File
import java.io.FileOutputStream

@Composable
fun QrPaymentScreen(
    totalPrice: Double,
    onBack: () -> Unit,
    onConfirmPayment: (receiptFile: File) -> Unit  // ✅ Cambiado: recibe el archivo
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Launcher para seleccionar imagen de la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Convertir URI a File temporal
            val tempFile = createTempFileFromUri(context, it)
            selectedImageFile = tempFile
            if (tempFile == null) {
                android.widget.Toast.makeText(
                    context,
                    "Error al procesar la imagen",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Launcher para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            android.widget.Toast.makeText(
                context,
                "Se necesita permiso para acceder a la galería",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Función para abrir la galería
    val openGallery = {
        when {
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU -> {
                when (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                )) {
                    android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                        galleryLauncher.launch("image/*")
                    }
                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
            }
            else -> {
                when (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )) {
                    android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                        galleryLauncher.launch("image/*")
                    }
                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }
    }

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

        // Código QR para pago
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

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para subir comprobante
        Button(
            onClick = { openGallery() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isUploading
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Subir Comprobante")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isUploading) "Procesando..." else "Subir Comprobante")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar imagen seleccionada
        if (selectedImageUri != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Comprobante de pago",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = {
                            selectedImageUri = null
                            selectedImageFile = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Eliminar comprobante",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Text(
                text = "Comprobante seleccionado correctamente",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Confirmar Pago - se desbloquea solo cuando hay imagen seleccionada
        PrimaryButton(
            text = "Confirmar Pago",
            onClick = {
                selectedImageFile?.let { file ->
                    isUploading = true
                    onConfirmPayment(file)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedImageFile != null && !isUploading
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Función auxiliar para convertir Uri a File
private fun createTempFileFromUri(context: android.content.Context, uri: Uri): File? {
    return try {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("receipt_", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}