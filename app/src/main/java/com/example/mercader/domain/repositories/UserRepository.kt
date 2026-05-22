package com.example.mercader.domain.repositories

import com.example.mercader.domain.models.UserProfile

interface UserRepository {
    suspend fun getUserProfile(userId: String): Result<UserProfile>
}
