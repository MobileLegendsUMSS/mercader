package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.UserProfileBaseResponse
import com.example.mercader.data.remote.models.FavoriteActionResponseDTO
import com.example.mercader.data.remote.models.FavoritesListResponseDTO
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApiService {

    @GET("perfil/usuarios/info-personal")
    suspend fun getUserProfile(): Response<UserProfileBaseResponse>

    @POST("perfil/usuarios/favoritos")
    suspend fun addFavorite(
        @Query("id_juego") gameId: String
    ): Response<FavoriteActionResponseDTO>

    @DELETE("perfil/usuarios/favoritos")
    suspend fun removeFavorite(
        @Query("id_juego") gameId: String
    ): Response<FavoriteActionResponseDTO>

    @GET("perfil/usuarios/favoritos")
    suspend fun getFavorites(): Response<FavoritesListResponseDTO>
}

