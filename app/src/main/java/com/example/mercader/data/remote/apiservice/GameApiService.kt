package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.dto.DeleteGameRequestDto
import com.example.mercader.data.remote.dto.DeleteGameResponseDto
import com.example.mercader.data.remote.models.*
import com.example.mercader.domain.models.Game
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.PartMap

interface GameApiService {

    @Multipart
    @POST("juegos/")
    suspend fun saveGame(
        @Query("idCategory") idCategory: String,
        @Part portada: okhttp3.MultipartBody.Part,
        @PartMap fields: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>,
        @Part services: List<okhttp3.MultipartBody.Part>
    ): Response<GameResponseDTO>

    @GET("categorias/")
    suspend fun getGameTypes(): Response<GameCategoryResponseDTO<List<GameCategory>>>

    @POST("categorias/")
    suspend fun createCategory(@Body category: CreateCategoryDTO): Response<GenericResponseDTO<GameCategory>>

    @retrofit2.http.PATCH("categorias/{id}")
    suspend fun editCategory(@retrofit2.http.Path("id") id: String, @Body category: CreateCategoryDTO): Response<GenericResponseDTO<GameCategory>>

    @retrofit2.http.DELETE("categorias/{id}")
    suspend fun deleteCategory(@retrofit2.http.Path("id") id: String): Response<GenericResponseDTO<Unit>>

    @GET("dificultades/")
    suspend fun getDifficulties(): Response<GameDifficultyResponseDTO<List<GameDifficulty>>>

    @GET("editoriales/")
    suspend fun getEditorials(): Response<GameEditorialResponseDTO<List<GameEditorial>>>

    @POST("editoriales/")
    suspend fun createEditorial(@Body editorial: CreateEditorialDTO): Response<GenericResponseDTO<GameEditorial>>

    @retrofit2.http.PATCH("editoriales/{id}")
    suspend fun editEditorial(@retrofit2.http.Path("id") id: String, @Body editorial: CreateEditorialDTO): Response<GenericResponseDTO<GameEditorial>>

    @retrofit2.http.DELETE("editoriales/{id}")
    suspend fun deleteEditorial(@retrofit2.http.Path("id") id: String): Response<GenericResponseDTO<Unit>>

    @GET("juegos/")
    suspend fun getGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>

    @GET("juegos/servicios")
    suspend fun getGameServices(
        @Query("id_juego") gameId: String
    ): Response<GameServicesResponseDTO>

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

    @Multipart
    @PATCH("juegos/")
    suspend fun updateGameWithImage(
        @Query("id_juego") gameId: String,
        @Part("fieldName") fieldName: RequestBody,
        @Part("fieldValue") fieldValue: RequestBody,
        @Part portada: MultipartBody.Part
    ): Response<Unit>

    @GET("juegos/sistema/recientes")
    suspend fun getRecentGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>

    @GET("juegos/sistema/visitados")
    suspend fun getMostVisitedGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>

    @GET("juegos/sistema/comprados")
    suspend fun getMostSoldGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>

    @GET("juegos/sistema/prestados")
    suspend fun getMostBorrowedGames(): Response<AllGamesResponseDTO<List<GameResponseDTO>>>
}