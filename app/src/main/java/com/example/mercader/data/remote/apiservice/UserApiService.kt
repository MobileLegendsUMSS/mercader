package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.UserProfileResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiService {

    @GET("usuarios/perfil/")
    suspend fun getUserProfile(
        @Query("id") userId: String
    ): Response<UserProfileResponseDTO>
}
