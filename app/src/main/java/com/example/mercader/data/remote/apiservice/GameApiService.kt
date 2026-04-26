package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.dto.DeleteGameRequestDto
import com.example.mercader.data.remote.dto.DeleteGameResponseDto
import com.example.mercader.data.remote.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface GameApiService {

    @POST("juegos/")
    suspend fun saveGame(
        @Body game: GameRequestDTO
    ): Response<GameResponseDTO>

    @GET("categorias/")
    suspend fun getGameTypes(): Response<GameCategoryResponseDTO<List<GameCategory>>>
    @GET("dificultades/")
    suspend fun getDifficulties(): Response<GameDifficultyResponseDTO<List<GameDifficulty>>>

    @GET("editoriales/")
    suspend fun getEditorials(): Response<GameEditorialResponseDTO<List<GameEditorial>>>

    @HTTP(method = "DELETE", path = "juegos/", hasBody = true)
    suspend fun deleteGame(
        @Query("id") id: String,
        @Body body: DeleteGameRequestDto
    ) : DeleteGameResponseDto
}