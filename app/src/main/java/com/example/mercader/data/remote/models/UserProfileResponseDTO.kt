package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

data class UserProfileResponseDTO(
    @SerializedName("_id") val id: String = "",
    @SerializedName("nombre_usuario") val username: String = "",
    @SerializedName("nombre") val name: String = "",
    @SerializedName("apellido") val lastName: String = "",
    @SerializedName("telefono") val phone: String = "",
    @SerializedName("correo") val email: String = "",
    @SerializedName("merca_points") val mercaPoints: Int = 0
)
