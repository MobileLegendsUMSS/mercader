package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.UserProfileBaseResponse
import com.example.mercader.data.remote.models.FavoriteActionResponseDTO
import com.example.mercader.data.remote.models.FavoritesListResponseDTO
import com.example.mercader.data.remote.models.PurchaseListResponseDTO
import com.example.mercader.data.remote.models.UserLoansRequestDTO
import com.example.mercader.data.remote.models.UserLoansListResponseDTO
import com.example.mercader.data.remote.models.EditProfileRequestDTO
import com.example.mercader.data.remote.models.TopGamesResponseDTO
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.HTTP
import retrofit2.http.Body
import retrofit2.http.PATCH

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

    @GET("servicios/usuarios/compra")
    suspend fun getUserPurchases(): Response<PurchaseListResponseDTO>

    @PATCH("/api/perfil/usuarios/info-personal")
    suspend fun editPersonalInfo(
        @Body updatedFields: EditProfileRequestDTO
    ): Response<UserProfileBaseResponse>

    @HTTP(method = "GET", path = "servicios/usuarios/prestamos", hasBody = true)
    suspend fun getUserLoans(
        @Body request: UserLoansRequestDTO
    ): Response<UserLoansListResponseDTO>

    @GET("reportes/usuarios/juegos-usados")
    suspend fun getTopGames(): Response<TopGamesResponseDTO>
}

