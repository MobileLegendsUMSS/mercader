package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

data class PurchaseListResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<PurchaseItemDTO>?
)

data class PurchaseItemDTO(
    @SerializedName("total") val total: Double,
    @SerializedName("metodo_pago") val paymentMethod: String,
    @SerializedName("detalles_carrito") val details: List<PurchaseDetailDTO>
)

data class PurchaseDetailDTO(
    @SerializedName("id_juego") val gameId: String,
    @SerializedName("titulo") val title: String,
    @SerializedName("cantidad_solicitada") val quantity: Int,
    @SerializedName("precio_juegos") val priceSubtotal: Double
)
