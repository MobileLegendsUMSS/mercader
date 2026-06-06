package com.example.mercader.domain.usecases

import com.example.mercader.data.local.ITokenRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationUseCase @Inject constructor(
    private val tokenRepository: ITokenRepository
) {

    suspend fun saveAuthTokens(accessToken: String, refreshToken: String, rol: String) {
        tokenRepository.saveTokens(accessToken, refreshToken, rol)
    }

    suspend fun getAccessToken(): String? {
        return tokenRepository.getAccessToken()
    }

    suspend fun getRefreshToken(): String? {
        return tokenRepository.getRefreshToken()
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
        tokenRepository.clearTokens()
    }

    fun isAdmin(rol: String?): Boolean {
        return rol == "admin" || rol == "superadmin"
    }
}