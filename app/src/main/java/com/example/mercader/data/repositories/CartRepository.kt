package com.example.mercader.data.repositories

import com.example.mercader.data.remote.models.CartItemResponse
import com.example.mercader.domain.models.CartItem

interface CartRepository {
    suspend fun addToCart(userId: String, gameId: String, quantity: Int): Result<Unit>
    suspend fun getCart(userId: String): Result<List<CartItemResponse>>
    suspend fun updateQuantity(userId: String, gameId: String, quantity: Int): Result<Unit>
    suspend fun removeFromCart(userId: String, gameId: String): Result<Unit>
}