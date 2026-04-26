package com.example.mercader.data.remote

import retrofit2.http.Body
import retrofit2.http.Query
import com.example.mercader.data.remote.dto.DeleteGameRequestDto
import com.example.mercader.data.remote.dto.DeleteGameResponseDto
import retrofit2.http.HTTP

interface ApiService {
    @HTTP(method = "DELETE",path = "juegos/", hasBody = true)
    suspend fun deleteGame(
        @Query("id") id: String,
        @Body body: DeleteGameRequestDto
    ) : DeleteGameResponseDto
}