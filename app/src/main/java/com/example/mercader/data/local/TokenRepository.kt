package com.example.mercader.data.local

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

/**
 * Repositorio para manejar almacenamiento y recuperación de token JWT
 * desde SharedPreferences del dispositivo
 * 
 * Responsabilidades:
 * - Guardar token en cache local después de login/signin
 * - Recuperar token para usar en peticiones HTTP
 * - Validar que no haya pasado más de 15 días sin usar la app
 * - Limpiar token cuando expire
 */
interface ITokenRepository {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun isTokenValid(): Boolean
    suspend fun clearToken()
    suspend fun updateLastAccessTime()
    suspend fun getDaysUntilExpiration(): Int
}

class TokenRepository(context: Context) : ITokenRepository {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("mercader_auth", Context.MODE_PRIVATE)
    
    companion object {
        private const val TOKEN_KEY = "jwt_token"
        private const val LAST_ACCESS_KEY = "last_access_time"
        private const val FIFTEEN_DAYS_MS = 15 * 24 * 60 * 60 * 1000L
    }
    
    /**
     * Guarda el token JWT y registra el tiempo de acceso actual
     * Se llama después de login/signin exitoso
     */
    override suspend fun saveToken(token: String) {
        sharedPreferences.edit().apply {
            putString(TOKEN_KEY, token)
            putLong(LAST_ACCESS_KEY, System.currentTimeMillis())
            apply()
        }
    }
    
    /**
     * Obtiene el token almacenado en cache
     * Retorna null si no existe o si ha expirado por inactividad
     */
    override suspend fun getToken(): String? {
        // Primero validar que no haya expirado por los 15 días
        if (!isTokenValid()) {
            clearToken()
            return null
        }
        
        return sharedPreferences.getString(TOKEN_KEY, null)
    }
    
    /**
     * Valida si el token sigue siendo válido (no han pasado 15 días sin usar)
     */
    override suspend fun isTokenValid(): Boolean {
        val token = sharedPreferences.getString(TOKEN_KEY, null) ?: return false
        val lastAccessTime = sharedPreferences.getLong(LAST_ACCESS_KEY, 0)
        
        if (lastAccessTime == 0L) return false
        
        val currentTime = System.currentTimeMillis()
        val timeSinceLastAccess = currentTime - lastAccessTime
        
        return timeSinceLastAccess <= FIFTEEN_DAYS_MS
    }
    
    /**
     * Limpia el token (desloguea al usuario)
     */
    override suspend fun clearToken() {
        sharedPreferences.edit().apply {
            remove(TOKEN_KEY)
            remove(LAST_ACCESS_KEY)
            apply()
        }
    }
    
    /**
     * Actualiza el tiempo del último acceso
     * Se debe llamar cada vez que la app se abre o cuando se realiza una acción importante
     */
    override suspend fun updateLastAccessTime() {
        sharedPreferences.edit().apply {
            putLong(LAST_ACCESS_KEY, System.currentTimeMillis())
            apply()
        }
    }
    override suspend fun getDaysUntilExpiration(): Int {
        val lastAccessTime = sharedPreferences.getLong(LAST_ACCESS_KEY, 0)

        if (lastAccessTime == 0L) return 0

        val currentTime = System.currentTimeMillis()
        val timeSinceLastAccess = currentTime - lastAccessTime
        val daysRemaining = (FIFTEEN_DAYS_MS - timeSinceLastAccess) / (24 * 60 * 60 * 1000L)

        return daysRemaining.toInt().coerceAtLeast(0)
    }
}
