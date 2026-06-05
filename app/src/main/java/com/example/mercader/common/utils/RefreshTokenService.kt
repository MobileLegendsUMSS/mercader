package com.example.mercader.common.utils

import android.util.Log
import com.example.mercader.data.local.ITokenRepository
import com.example.mercader.data.remote.apiservice.AuthApiService
import com.example.mercader.data.remote.apiservice.RefreshTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshTokenService @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenRepository: ITokenRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var refreshJob: kotlinx.coroutines.Job? = null
    private val mutex = Mutex()
    private var isRefreshing = false
    private var onRoleChangedCallback: (() -> Unit)? = null

    fun startPeriodicRefresh(onRoleChanged: () -> Unit) {
        onRoleChangedCallback = onRoleChanged
        refreshJob?.cancel()
        refreshJob = scope.launch {
            while (true) {
                delay(60_000) // Cada 1 minuto
                refreshTokenIfNeeded()
            }
        }
    }

    fun stopPeriodicRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    private suspend fun refreshTokenIfNeeded() {
        mutex.withLock {
            if (isRefreshing) return
            isRefreshing = true
        }

        try {
            val refreshToken = tokenRepository.getRefreshToken()
            if (refreshToken.isNullOrEmpty()) {
                Log.d("RefreshToken", "No refresh token available")
                return
            }

            Log.d("RefreshToken", "Refreshing token...")
            val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))

            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                // Guardar nuevos tokens
                tokenRepository.saveTokens(
                    body.accessToken,
                    body.refreshToken,
                    body.rol ?: tokenRepository.getRol() ?: "usuario"
                )
                tokenRepository.updateLastAccessTime()
                Log.d("RefreshToken", "Token refreshed successfully")

                // Si el rol cambió, notificar para logout
                val currentRol = tokenRepository.getRol()
                if (body.rol != null && currentRol != body.rol) {
                    Log.d("RefreshToken", "Role changed from $currentRol to ${body.rol}")
                    onRoleChangedCallback?.invoke()
                }
            } else {
                // Token inválido - hacer logout
                Log.e("RefreshToken", "Refresh failed: ${response.code()}")
                if (response.code() == 401) {
                    onRoleChangedCallback?.invoke()
                }
            }
        } catch (e: Exception) {
            Log.e("RefreshToken", "Error refreshing token: ${e.message}")
        } finally {
            mutex.withLock {
                isRefreshing = false
            }
        }
    }

    suspend fun forceLogout() {
        try {
            val refreshToken = tokenRepository.getRefreshToken()
            if (!refreshToken.isNullOrEmpty()) {
                authApiService.logout(RefreshTokenRequest(refreshToken))
            }
        } catch (e: Exception) {
            Log.e("RefreshToken", "Error during logout: ${e.message}")
        } finally {
            tokenRepository.clearTokens()
        }
    }
}