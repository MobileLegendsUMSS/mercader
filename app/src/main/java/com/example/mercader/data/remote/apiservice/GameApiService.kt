package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GameApiService {

    @POST("api/juegos")
    suspend fun saveGame(
        @Body game: GameRequestDTO
    ): Response<GameResponseDTO>

    @GET("api/categorias")
    suspend fun getGameTypes(): Response<GameCategoryResponseDTO<List<GameCategory>>>
    @GET("api/dificultades")
    suspend fun getDifficulties(): Response<GameDifficultyResponseDTO<List<GameDifficulty>>>

    @GET("api/editoriales")
    suspend fun getEditorials(): Response<GameEditorialResponseDTO<List<GameEditorial>>>
}