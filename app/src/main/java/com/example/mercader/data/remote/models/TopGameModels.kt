package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

data class TopGameDTO(
    @SerializedName("titulo") val title: String,
    @SerializedName("cantidad_prestamos") val loanCount: Int
)

data class TopGamesResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<TopGameDTO>?
)
