package com.example.mercader.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ITokenRepository {
    suspend fun saveToken(token: String, rol: String)
    suspend fun getToken(): String?
    suspend fun getRol(): String?
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
        private const val ROL_KEY = "user_rol"
        private const val LAST_ACCESS_KEY = "last_access_time"
        private const val FIFTEEN_DAYS_MS = 15 * 24 * 60 * 60 * 1000L
    }

    override suspend fun saveToken(token: String, rol: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putString(TOKEN_KEY, token)
                putString(ROL_KEY, rol)
                putLong(LAST_ACCESS_KEY, System.currentTimeMillis())
                apply()
            }
        }
    }

    override suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            if (!isTokenValid()) {
                clearToken()
                return@withContext null
            }
            sharedPreferences.getString(TOKEN_KEY, null)
        }
    }

    override suspend fun getRol(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(ROL_KEY, null)
        }
    }

    override suspend fun isTokenValid(): Boolean {
        return withContext(Dispatchers.IO) {
            val token = sharedPreferences.getString(TOKEN_KEY, null)
            if (token == null) return@withContext false

            val lastAccessTime = sharedPreferences.getLong(LAST_ACCESS_KEY, 0)
            if (lastAccessTime == 0L) return@withContext false

            val currentTime = System.currentTimeMillis()
            val timeSinceLastAccess = currentTime - lastAccessTime

            timeSinceLastAccess <= FIFTEEN_DAYS_MS
        }
    }

    override suspend fun clearToken() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                remove(TOKEN_KEY)
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