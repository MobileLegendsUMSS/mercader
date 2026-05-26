package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

data class FavoriteRequestDTO(
    @SerializedName("id_juego") val gameId: String
)

data class FavoriteActionResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class FavoriteCheckResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: FavoriteCheckDataDTO?
)

data class FavoriteCheckDataDTO(
    @SerializedName("isFavorite") val isFavorite: Boolean
)

data class FavoriteGameDTO(
    @SerializedName("id_juego") val id_juego: String?,
    @SerializedName("_id") val mongoId: String?,
    @SerializedName("titlo") val titlo: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("precio") val precio: Float?,
    @SerializedName("cant_min_pers") val cantMinPers: Int?,
    @SerializedName("cant_max_pers") val cantMaxPers: Int?,
    @SerializedName("duracion_min") val duracionMin: Int?,
    @SerializedName("duracion_max") val duracionMax: Int?,
    @SerializedName("disponible") val disponible: Boolean?
)

data class FavoritesListResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<FavoriteGameDTO>?
)
