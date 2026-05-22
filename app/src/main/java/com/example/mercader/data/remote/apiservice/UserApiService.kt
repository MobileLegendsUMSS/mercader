package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.UserProfileBaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiService {

    @GET("perfil/usuarios/info-personal")
    suspend fun getUserProfile(
        @Query("id_usuario") userId: String
    ): Response<UserProfileBaseResponse>
}
