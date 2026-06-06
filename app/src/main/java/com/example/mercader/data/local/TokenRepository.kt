package com.example.mercader.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ITokenRepository {
    suspend fun saveTokens(accessToken: String, refreshToken: String, rol: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getRol(): String?
    suspend fun isTokenValid(): Boolean
    suspend fun clearTokens()
    suspend fun updateLastAccessTime()
    suspend fun getDaysUntilExpiration(): Int
}

class TokenRepository(context: Context) : ITokenRepository {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("mercader_auth", Context.MODE_PRIVATE)

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val ROL_KEY = "user_rol"
        private const val LAST_ACCESS_KEY = "last_access_time"
        private const val FIFTEEN_DAYS_MS = 15 * 24 * 60 * 60 * 1000L
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String, rol: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putString(ACCESS_TOKEN_KEY, accessToken)
                putString(REFRESH_TOKEN_KEY, refreshToken)
                putString(ROL_KEY, rol)
                putLong(LAST_ACCESS_KEY, System.currentTimeMillis())
                apply()
            }
        }
    }

    override suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            if (!isTokenValid()) {
                clearTokens()
                return@withContext null
            }
            sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
        }
    }

    override suspend fun getRefreshToken(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
        }
    }

    override suspend fun getRol(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(ROL_KEY, null)
        }
    }

    override suspend fun isTokenValid(): Boolean {
        return withContext(Dispatchers.IO) {
            val token = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
            if (token == null) return@withContext false

            val lastAccessTime = sharedPreferences.getLong(LAST_ACCESS_KEY, 0)
            if (lastAccessTime == 0L) return@withContext false

            val currentTime = System.currentTimeMillis()
            val timeSinceLastAccess = currentTime - lastAccessTime

            timeSinceLastAccess <= FIFTEEN_DAYS_MS
        }
    }

    override suspend fun clearTokens() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                remove(ACCESS_TOKEN_KEY)
                remove(REFRESH_TOKEN_KEY)
                remove(ROL_KEY)
                remove(LAST_ACCESS_KEY)
                apply()
            }
        }
    }

    override suspend fun updateLastAccessTime() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putLong(LAST_ACCESS_KEY, System.currentTimeMillis())
                apply()
            }
        }
    }

    override suspend fun getDaysUntilExpiration(): Int {
        return withContext(Dispatchers.IO) {
            val lastAccessTime = sharedPreferences.getLong(LAST_ACCESS_KEY, 0)
            if (lastAccessTime == 0L) return@withContext 0

            val currentTime = System.currentTimeMillis()
            val timeSinceLastAccess = currentTime - lastAccessTime
            val daysRemaining = (FIFTEEN_DAYS_MS - timeSinceLastAccess) / (24 * 60 * 60 * 1000L)
            daysRemaining.toInt().coerceAtLeast(0)
        }
    }
}