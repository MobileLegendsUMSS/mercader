package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

data class EditProfileRequestDTO(
    @SerializedName("nombres")
    val nombres: String? = null,

    @SerializedName("apellidos")
    val apellidos: String? = null,

    @SerializedName("telefono")
    val telefono: String? = null,

    @SerializedName("correo_contacto")
    val correoContacto: String? = null
)