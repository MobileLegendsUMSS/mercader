package com.example.mercader.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val ADMIN_SUFFIX = "#adm"

@Composable
fun LoginScreen(
    onLoginSuccess: (isAdmin: Boolean) -> Unit,
    onNavigateToSignup: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var contrasenna by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasenna,
            onValueChange = { contrasenna = it },
            label = { Text("Contraseña") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Usa el sufijo $ADMIN_SUFFIX para acceder como admin",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = {
                val nameTrimmed = nombre.trim()
                if (nameTrimmed.isEmpty()) {
                    errorMessage = "El nombre es requerido"
                    return@Button
                }
                if (contrasenna.length < 6) {
                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    return@Button
                }
                errorMessage = null
                
                // TODO: IMPLEMENTAR LLAMADA API
                // 1. Hacer petición POST a mercader-server: /api/login
                // 2. Con body: { nombre, contrasenna }
                // 3. Esperar respuesta con estructura:
                //    {
                //      mensaje: string,
                //      token: string (JWT),  ← AQUI ESTA EL TOKEN
                //      usuario: { id, nombre }
                //    }
                // 4. SI la respuesta es exitosa:
                //    a) GUARDAR el token en CACHE del dispositivo usando TokenRepository
                //    b) Guardar timestamp actual como lastAccessTime (para validar 15 días)
                //    c) Luego llamar a onLoginSuccess(isAdmin)
                // 5. SI falla: mostrar errorMessage
                
                val isAdmin = contrasenna.endsWith(ADMIN_SUFFIX)
                onLoginSuccess(isAdmin)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onNavigateToSignup) {
            Text("¿No tienes cuenta?")
        }
    }
}

@Composable
fun SignupScreen(
    onSignupSuccess: (isAdmin: Boolean) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var contrasenna by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasenna,
            onValueChange = { contrasenna = it },
            label = { Text("Contraseña") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Si la contraseña termina con $ADMIN_SUFFIX verás la vista de admin",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = {
                val nameTrimmed = nombre.trim()
                if (nameTrimmed.isEmpty()) {
                    errorMessage = "El nombre es requerido"
                    return@Button
                }
                if (contrasenna.length < 6) {
                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    return@Button
                }
                errorMessage = null
                
                // TODO: IMPLEMENTAR LLAMADA API
                // 1. Hacer petición POST a mercader-server: /api/signin
                // 2. Con body: { nombre, contrasenna }
                // 3. Esperar respuesta con estructura:
                //    {
                //      mensaje: string,
                //      token: string (JWT),  ← AQUI ESTA EL TOKEN (debe devolver después de fix)
                //      usuario: { id, nombre }
                //    }
                // 4. SI la respuesta es exitosa:
                //    a) GUARDAR el token en CACHE del dispositivo usando TokenRepository
                //    b) Guardar timestamp actual como lastAccessTime (para validar 15 días)
                //    c) Luego llamar a onSignupSuccess(isAdmin)
                // 5. SI falla: mostrar errorMessage con detalles (ej: "Usuario ya existe")
                //
                // NOTA: El backend actualmente NO devuelve token en signin.
                // Debe agregarse generación y devolución del token en signin.service.ts
                
                val isAdmin = contrasenna.endsWith(ADMIN_SUFFIX)
                onSignupSuccess(isAdmin)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Ya tengo cuenta")
        }
    }
}
