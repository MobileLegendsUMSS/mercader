package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

data class GameServicesResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("services") val services: List<String>? = emptyList(),
    @SerializedName("message") val message: String? = null
)