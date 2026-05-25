package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

data class UserProfileBaseResponse(
    @SerializedName("result") val result: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: UserProfileDataDTO?
)

data class UserProfileDataDTO(
    @SerializedName("nombre") val username: String? = "",
    @SerializedName("nombres") val name: String? = "",
    @SerializedName("apellidos") val lastName: String? = "",
    @SerializedName("telefono") val phone: String? = "",
    @SerializedName("correo_contacto") val email: String? = "",
    @SerializedName("mercapoints") val mercaPoints: Int? = 0
)
