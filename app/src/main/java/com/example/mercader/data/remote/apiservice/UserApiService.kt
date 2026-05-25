package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.UserProfileBaseResponse
import com.example.mercader.data.remote.models.FavoriteRequestDTO
import com.example.mercader.data.remote.models.FavoriteActionResponseDTO
import com.example.mercader.data.remote.models.FavoriteCheckResponseDTO
import com.example.mercader.data.remote.models.FavoritesListResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApiService {

    @GET("perfil/usuarios/info-personal")
    suspend fun getUserProfile(
        @Query("id_usuario") userId: String
    ): Response<UserProfileBaseResponse>

    @GET("usuarios/favoritos/check")
    suspend fun checkFavorite(
        @Query("id_juego") gameId: String
    ): Response<FavoriteCheckResponseDTO>

    @POST("usuarios/favoritos")
    suspend fun addFavorite(
        @Body body: FavoriteRequestDTO
    ): Response<FavoriteActionResponseDTO>

    @HTTP(method = "DELETE", path = "usuarios/favoritos", hasBody = true)
    suspend fun removeFavorite(
        @Body body: FavoriteRequestDTO
    ): Response<FavoriteActionResponseDTO>

    @GET("usuarios/favoritos")
    suspend fun getFavorites(): Response<FavoritesListResponseDTO>
}

