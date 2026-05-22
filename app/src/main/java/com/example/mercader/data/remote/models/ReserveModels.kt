package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ReserveRequest(
    @SerializedName("servicio")
    val servicio: String,  // "prestamo" o "alquiler"
    @SerializedName("fecha_prestamo")
    val fechaPrestamo: String  // ISO 8601: "2025-05-20T14:30:00"
)

// Respuesta del servidor
data class ReserveResponse(
    val success: Boolean,
    val message: String,
    val data: ReserveData? = null
)

// Datos del préstamo (opcional, si el servidor devuelve info)
data class ReserveData(
    @SerializedName("id_prestamo")
    val idPrestamo: String,
    @SerializedName("fecha_limite")
    val fechaLimite: String? = null
)

// Body para filtrar préstamos (GET)
data class ReserveFilterRequest(
    val vigente: Boolean = false,
    val collected: Boolean = false,
    val returned: Boolean = false
)