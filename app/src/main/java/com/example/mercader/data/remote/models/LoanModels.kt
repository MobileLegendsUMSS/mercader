package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

data class UserLoansRequestDTO(
    @SerializedName("vigent") val vigent: Boolean,
    @SerializedName("collected") val collected: Boolean,
    @SerializedName("returned") val returned: Boolean
)

data class UserLoansListResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<LoanItemDTO>?
)

data class LoanItemDTO(
    @SerializedName("id_prestamo") val loanId: String,
    @SerializedName("titulo") val title: String,
    @SerializedName("descripcion") val description: String?,
    @SerializedName("servicio") val service: String,
    @SerializedName("fecha_solicitud") val requestDate: String,
    @SerializedName("fecha_limite") val limitDate: String,
    @SerializedName("fecha_inicio") val startDate: String?,
    @SerializedName("fecha_fin") val endDate: String?
)

data class UpdateLoanRequestDTO(
    @SerializedName("fecha_inicio") val fechaInicio: String? = null,
    @SerializedName("fecha_fin") val fechaFin: String? = null
)

data class LoanActionResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)
