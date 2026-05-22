package com.example.mercader.data.repositories

import com.example.mercader.data.remote.apiservice.UserApiService
import com.example.mercader.domain.models.UserProfile
import com.example.mercader.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService
) : UserRepository {

    override suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val response = userApiService.getUserProfile(userId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.result && body.data != null) {
                    val dto = body.data
                    Result.success(
                        UserProfile(
                            id = userId,
                            username = dto.username ?: "",
                            name = dto.name ?: "",
                            lastName = dto.lastName ?: "",
                            phone = dto.phone ?: "",
                            email = dto.email ?: "",
                            mercaPoints = dto.mercaPoints ?: 0
                        )
                    )
                } else {
                    Result.failure(Exception(body?.message ?: "Respuesta vacía del servidor"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
