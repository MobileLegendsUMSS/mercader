package com.example.mercader.data.remote.models

import com.google.gson.annotations.SerializedName

// Request para agregar o actualizar cantidad
data class CartRequest(
    @SerializedName("id_usuario")
    val idUsuario: String,
    @SerializedName("id_juego")
    val idJuego: String,
    val cantidad: Int
)

// Request para eliminar del carrito
data class DeleteFromCartRequest(
    @SerializedName("id_usuario")
    val idUsuario: String,
    @SerializedName("id_juego")
    val idJuego: String
)

// Respuesta del carrito
data class CartResponse(
    val success: Boolean,
    val message: String,
    val data: List<CartItemResponse>? = null
)

// Item del carrito en la respuesta
data class CartItemResponse(
    @SerializedName("id_juego")
    val idJuego: String,
    val titulo: String,
    @SerializedName("cantidad_solicitada")
    val cantidadSolicitada: Int,
    @SerializedName("precio_juegos")
    val precioJuegos: Double
)

data class BuyRequest(
    @SerializedName("id_usuario")
    val idUsuario: String,
    @SerializedName("id_metodo_pago")
    val idMetodoPago: String
)

// Respuesta de compra
data class BuyResponse(
    val success: Boolean,
    val message: String
)