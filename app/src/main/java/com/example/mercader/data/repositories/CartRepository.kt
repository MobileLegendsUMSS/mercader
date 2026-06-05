package com.example.mercader.data.repositories

import com.example.mercader.data.remote.models.CartItemResponse
import java.io.File

interface CartRepository {
    suspend fun addToCart(gameId: String, quantity: Int): Result<Unit>
    suspend fun getCart(): Result<List<CartItemResponse>>
    suspend fun updateQuantity(gameId: String, quantity: Int): Result<Unit>
    suspend fun removeFromCart(gameId: String): Result<Unit>
    suspend fun checkout(metodoPagoId: String): Result<Unit>

    suspend fun checkoutWithReceipt(
        metodoPagoId: String,
        receiptFile: File
    ): Result<Unit>
}