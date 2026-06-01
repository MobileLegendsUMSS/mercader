package com.example.mercader.common.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.domain.usecases.AuthenticationUseCase
import com.example.mercader.ui.screens.auth.LoginScreen
import androidx.compose.ui.Modifier
import com.example.mercader.ui.screens.auth.AuthViewModel

/**
 * Splash/Loading screen que valida el token al iniciar la app
 * 
 * Flujo:
 * 1. Mostrar loading
 * 2. Verificar si token válido en cache
 * 3. Si válido: Ir al home (admin/user)
 * 4. Si inválido: Ir al login
 * 5. Si token expiró (15 días): Desloguear y Cir al login
 * 
 * Usar en MainActivity como primera pantalla
 */
@Composable
fun SplashAuthenticationScreen(
    onNavigateToHome: (isAdmin: Boolean, rol: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.checkAuthentication(
            onAuthenticated = { isAdmin, rol -> onNavigateToHome(isAdmin, rol) },
            onNotAuthenticated = { onNavigateToLogin() }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}