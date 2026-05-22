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
                val dto = response.body()
                if (dto != null) {
                    Result.success(
                        UserProfile(
                            id = dto.id,
                            username = dto.username,
                            name = dto.name,
                            lastName = dto.lastName,
                            phone = dto.phone,
                            email = dto.email,
                            mercaPoints = dto.mercaPoints
                        )
                    )
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
