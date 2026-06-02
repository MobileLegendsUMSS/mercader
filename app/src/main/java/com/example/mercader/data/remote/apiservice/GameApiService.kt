package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.dto.DeleteGameRequestDto
import com.example.mercader.data.remote.dto.DeleteGameResponseDto
import com.example.mercader.data.remote.models.*
import com.example.mercader.domain.models.Game
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface GameApiService {

    @POST("juegos/")
    suspend fun saveGame(
        @Body game: GameRequestDTO,
        @Query("idCategory") idCategory:String
    ): Response<GameResponseDTO>

    @GET("categorias/")
    suspend fun getGameTypes(): Response<GameCategoryResponseDTO<List<GameCategory>>>
    @GET("dificultades/")
    suspend fun getDifficulties(): Response<GameDifficultyResponseDTO<List<GameDifficulty>>>

    @GET("editoriales/")
    suspend fun getEditorials(): Response<GameEditorialResponseDTO<List<GameEditorial>>>

    @GET("juegos/")
    suspend fun getGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>

    @PATCH("juegos/")
    suspend fun editGame(
        @Query("id_juego") id: String,
        @Body gameEdit: GameEditDTO
    ): Response<GameEditResponseDTO>

    @HTTP(method = "DELETE", path = "juegos/", hasBody = true)
    suspend fun deleteGame(
        @Query("id") id: String,
        @Body body: DeleteGameRequestDto
    ) : DeleteGameResponseDto

    @GET("juegos/sistema/recientes")
    suspend fun getRecentGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>

    @GET("juegos/sistema/visitados")
    suspend fun getMostVisitedGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>

    @GET("juegos/sistema/comprados")
    suspend fun getMostSoldGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>

    @GET("juegos/sistema/prestados")
    suspend fun getMostBorrowedGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>
}