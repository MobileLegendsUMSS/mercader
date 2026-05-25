package com.example.mercader.domain.usecases

import com.example.mercader.data.local.ITokenRepository
import javax.inject.Inject

/**
 * UseCase para manejar la lógica de autenticación y validación de token
 * 
 * Flujo:
 * 1. Al iniciar app: verificar si token válido (< 15 días)
 * 2. Si válido: mantener sesión iniciada
 * 3. Si inválido: llevar a login
 * 4. Cada acción: actualizar timestamp de acceso
 */
class AuthenticationUseCase @Inject constructor(
    private val tokenRepository: ITokenRepository
) {
    
    /**
     * Verifica si el usuario ya está autenticado y su token es válido
     * Se llama en MainActivity.onCreate() o SplashScreen
     */
    suspend fun isUserAuthenticated(): Boolean {
        val token = tokenRepository.getToken()
        return token != null && tokenRepository.isTokenValid()
    }
    
    /**
     * Obtiene el token actual para usarlo en peticiones HTTP
     * IMPORTANTE: Validar que isUserAuthenticated() retorne true antes de llamar
     */
    suspend fun getAuthToken(): String? {
        return tokenRepository.getToken()
    }
    
    /**
     * Se llama después de login/signin exitoso
     * Guarda el token y lo prepara para uso
     */
    suspend fun saveAuthToken(token: String) {
        tokenRepository.saveToken(token)
    }
    
    /**
     * Se llama cuando el usuario hace logout o cuando expira token
     */
    suspend fun logout() {
        tokenRepository.clearToken()
    }
    
    /**
     * Se debe llamar regularmente (cada vez que abre la app, hace una acción importante)
     * Para resetear el contador de los 15 días
     */
    suspend fun refreshAccessTime() {
        tokenRepository.updateLastAccessTime()
    }
    
    /**
     * Retorna los días restantes antes de que expire por inactividad
     * Útil para mostrar warning al usuario
     */
    suspend fun getDaysUntilExpiration(): Int {
        return tokenRepository.getDaysUntilExpiration()
    }
}
