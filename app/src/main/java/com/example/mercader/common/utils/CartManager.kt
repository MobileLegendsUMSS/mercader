package com.example.mercader.common.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.mercader.domain.models.CartItem
import com.example.mercader.domain.models.Game

class CartManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val CART_KEY = "shopping_cart"
        private var instance: CartManager? = null

        fun getInstance(context: Context): CartManager {
            if (instance == null) {
                instance = CartManager(context.applicationContext)
            }
            return instance!!
        }
    }

    // Añadir juego al carrito (con cantidad 1)
    fun addToCart(game: Game): Boolean {
        val currentCart = getCart().toMutableList()
        val existingItem = currentCart.find { it.game.id == game.id }

        if (existingItem != null) {
            // Si ya existe, incrementar cantidad
            existingItem.quantity++
            saveCart(currentCart)
        } else {
            // Si no existe, agregar nuevo item
            currentCart.add(CartItem(game = game, quantity = 1))
            saveCart(currentCart)
        }
        return true
    }

    // Obtener todos los items del carrito
    fun getCart(): List<CartItem> {
        val json = prefs.getString(CART_KEY, "[]")
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type)
    }

    // Actualizar cantidad de un juego
    fun updateQuantity(gameId: String, newQuantity: Int): Boolean {
        if (newQuantity <= 0) {
            return removeFromCart(gameId)
        }

        val currentCart = getCart().toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.game.id == gameId }

        if (itemIndex != -1) {
            currentCart[itemIndex] = currentCart[itemIndex].copy(quantity = newQuantity)
            saveCart(currentCart)
            return true
        }
        return false
    }

    // Incrementar cantidad
    fun incrementQuantity(gameId: String): Boolean {
        val currentCart = getCart().toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.game.id == gameId }

        if (itemIndex != -1) {
            val newQuantity = currentCart[itemIndex].quantity + 1
            currentCart[itemIndex] = currentCart[itemIndex].copy(quantity = newQuantity)
            saveCart(currentCart)
            return true
        }
        return false
    }

    // Decrementar cantidad
    fun decrementQuantity(gameId: String): Boolean {
        val currentCart = getCart().toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.game.id == gameId }

        if (itemIndex != -1) {
            val newQuantity = currentCart[itemIndex].quantity - 1
            if (newQuantity <= 0) {
                return removeFromCart(gameId)
            } else {
                currentCart[itemIndex] = currentCart[itemIndex].copy(quantity = newQuantity)
                saveCart(currentCart)
                return true
            }
        }
        return false
    }

    // Remover juego del carrito
    fun removeFromCart(gameId: String): Boolean {
        val currentCart = getCart().toMutableList()
        val removed = currentCart.removeAll { it.game.id == gameId }
        if (removed) {
            saveCart(currentCart)
        }
        return removed
    }

    // Vaciar carrito
    fun clearCart() {
        saveCart(emptyList())
    }

    // Obtener cantidad total de items (suma de cantidades)
    fun getTotalItemCount(): Int {
        return getCart().sumOf { it.quantity }
    }

    // Obtener precio total del carrito
    fun getTotalPrice(): Double {
        return getCart().sumOf { it.game.price * it.quantity.toDouble() }
    }

    // Verificar si un juego está en el carrito
    fun isInCart(gameId: String): Boolean {
        return getCart().any { it.game.id == gameId }
    }

    // Obtener cantidad de un juego específico
    fun getQuantity(gameId: String): Int {
        return getCart().find { it.game.id == gameId }?.quantity ?: 0
    }

    private fun saveCart(cart: List<CartItem>) {
        val json = gson.toJson(cart)
        prefs.edit().putString(CART_KEY, json).apply()
    }
}