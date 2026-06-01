package com.example.mercader.domain.usecases

import com.example.mercader.data.local.ITokenRepository  // ← Importar la interfaz
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationUseCase @Inject constructor(
    private val tokenRepository: ITokenRepository
) {

    suspend fun saveAuthToken(token: String, rol: String) {
        tokenRepository.saveToken(token, rol)
    }

    suspend fun getAuthToken(): String? {
        return tokenRepository.getToken()
    }

    suspend fun getUserRol(): String? {
        return tokenRepository.getRol()
    }

    suspend fun isUserAuthenticated(): Boolean {
        return tokenRepository.isTokenValid()
    }

    suspend fun refreshAccessTime() {
        tokenRepository.updateLastAccessTime()
    }

    suspend fun logout() {
        tokenRepository.clearToken()
    }

    fun isAdmin(rol: String?): Boolean {
        return rol == "admin" || rol == "superadmin"
    }
}