package com.example.mercader.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private const val ADMIN_SUFFIX = "#adm"

@Composable
fun LoginScreen(
    onLoginSuccess: (isAdmin: Boolean) -> Unit,
    onNavigateToSignup: () -> Unit,
    viewModel: AuthViewModel = viewModel() // Inyectamos el ViewModel de Hilt/Compose
) {
    var nombre by remember { mutableStateOf("") }
    var contrasenna by remember { mutableStateOf("") }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    // Observamos el estado del flujo de autenticación del backend
    val authState by viewModel.authState.collectAsState()

    // Reaccionar cuando el backend responde exitosamente
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onLoginSuccess((authState as AuthState.Success).isAdmin)
                viewModel.resetState() // Limpiar estado al salir
            }
            else -> {}
        }
    }

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
            label = { Text("Nombre de Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasenna,
            onValueChange = { contrasenna = it },
            label = { Text("Contraseña") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Usa el sufijo $ADMIN_SUFFIX para acceder como admin",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val currentError = localErrorMessage ?: (authState as? AuthState.Error)?.message
        currentError?.let {
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
                    localErrorMessage = "El nombre es requerido"
                    return@Button
                }
                if (contrasenna.length < 6) {
                    localErrorMessage = "La contraseña debe tener al menos 6 caracteres"
                    return@Button
                }
                localErrorMessage = null

                viewModel.login(nameTrimmed, contrasenna)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Ingresar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onNavigateToSignup,
            enabled = authState !is AuthState.Loading
        ) {
            Text("¿No tienes cuenta?")
        }
    }
}

@Composable
fun SignupScreen(
    onSignupSuccess: (isAdmin: Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // ---- Estados para los campos requeridos por el Backend ----
    var nombre by remember { mutableStateOf("") }
    var contrasenna by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correoContacto by remember { mutableStateOf("") }

    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onSignupSuccess((authState as AuthState.Success).isAdmin)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Usamos verticalScroll para evitar desbordamientos de pantalla debido a la cantidad de campos
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo: Nombre de Usuario
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Contraseña
        OutlinedTextField(
            value = contrasenna,
            onValueChange = { contrasenna = it },
            label = { Text("Contraseña") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Nombres (Opcional en backend pero ideal mandarlo)
        OutlinedTextField(
            value = nombres,
            onValueChange = { nombres = it },
            label = { Text("Nombres (Opcional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Apellidos (Opcional en backend pero ideal mandarlo)
        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos (Opcional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Teléfono (Estrictamente requerido)
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono de contacto") },
            placeholder = { Text("Ej: 71234567") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Correo de contacto (Estrictamente requerido)
        OutlinedTextField(
            value = correoContacto,
            onValueChange = { correoContacto = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Si la contraseña termina con $ADMIN_SUFFIX verás la vista de admin",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        val currentError = localErrorMessage ?: (authState as? AuthState.Error)?.message
        currentError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = {
                val nameTrimmed = nombre.trim()
                val phoneTrimmed = telefono.trim()
                val emailTrimmed = correoContacto.trim()

                // Validaciones locales rápidas antes de ir al servidor
                if (nameTrimmed.isEmpty()) {
                    localErrorMessage = "El nombre de usuario es requerido"
                    return@Button
                }
                if (contrasenna.length < 6) {
                    localErrorMessage = "La contraseña debe tener al menos 6 caracteres"
                    return@Button
                }
                if (phoneTrimmed.isEmpty()) {
                    localErrorMessage = "El teléfono de contacto es requerido"
                    return@Button
                }
                if (emailTrimmed.isEmpty()) {
                    localErrorMessage = "El correo electrónico es requerido"
                    return@Button
                }
                localErrorMessage = null

                // Modifica los parámetros de firma en tu AuthViewModel.signin si es necesario
                // pasándole las nuevas propiedades.
                viewModel.signin(
                    nombre = nameTrimmed,
                    contrasenna = contrasenna,
                    nombres = nombres.trim().ifEmpty { null },
                    apellidos = apellidos.trim().ifEmpty { null },
                    telefono = phoneTrimmed,
                    correo_contacto = emailTrimmed
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Registrar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onNavigateToLogin,
            enabled = authState !is AuthState.Loading
        ) {
            Text("Ya tengo cuenta")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}