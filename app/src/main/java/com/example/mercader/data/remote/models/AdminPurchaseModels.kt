package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

// Respuesta para lista de compras (admin)
data class AdminPurchaseListResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<AdminPurchaseItemDTO>?
)

// Item de compra para admin
data class AdminPurchaseItemDTO(
    @SerializedName("id_compra") val idCompra: String,
    @SerializedName("total") val total: Double,
    @SerializedName("estado") val estado: String,
    @SerializedName("comprobante") val comprobante: String,
    @SerializedName("metodo_pago") val metodoPago: String,
    @SerializedName("detalles_carrito") val detallesCarrito: List<PurchaseDetailDTO>
)

// Detalle de compra individual (admin)
data class AdminPurchaseDetailResponseDTO(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: AdminPurchaseDetailDTO?
)

data class AdminPurchaseDetailDTO(
    @SerializedName("nombre_usuario") val nombreUsuario: String,
    @SerializedName("nombres") val nombres: String?,
    @SerializedName("apellidos") val apellidos: String?,
    @SerializedName("total") val total: Double,
    @SerializedName("estado") val estado: String,
    @SerializedName("comprobante") val comprobante: String,
    @SerializedName("fecha_creacion") val fechaCreacion: String,
    @SerializedName("detalles_carrito") val detallesCarrito: List<PurchaseDetailDTO>
)

// Request para aceptar/rechazar compra
data class UpdatePurchaseStateRequest(
    @SerializedName("id_compra") val idCompra: String,
    @SerializedName("acceptance") val acceptance: String  // "aceptado" o "rechazado"
)

// Respuesta al actualizar estado
data class UpdatePurchaseStateResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)