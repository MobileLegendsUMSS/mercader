package com.example.mercader.common.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.mercader.domain.models.CartItem
import com.example.mercader.domain.models.Game
import com.example.mercader.data.repositories.CartRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManager @Inject constructor(
    private val context: Context,
    private val cartRepository: CartRepository
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val CART_KEY = "shopping_cart"

        private const val HARDCODED_METODO_PAGO_ID = "64f1a2b3c4d5e6f7a8b9c0d4"
    }

    // Añadir juego al carrito (backend + cache)
    suspend fun addToCart(game: Game): Boolean {
        return try {
            val result = cartRepository.addToCart(game.id, 1)
            if (result.isSuccess) {
                // Actualizar cache local
                updateLocalCacheFromBackend()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Obtener todos los items del carrito (desde backend)
    suspend fun getCart(): List<CartItem> {
        return try {
            val result = cartRepository.getCart()
            if (result.isSuccess && result.getOrNull() != null) {
                val cartItems = result.getOrNull() ?: emptyList()
                // Convertir a CartItem local y guardar en cache
                val localItems = cartItems.map { response ->
                    CartItem(
                        game = Game(
                            id = response.idJuego,
                            title = response.titulo,
                            description = "",
                            tutorial = "",
                            category = com.example.mercader.data.remote.models.Category("", ""),
                            nMinPerson = 0,
                            nMaxPerson = 0,
                            minMinutes = 0,
                            maxMinutes = 0,
                            difficulty = com.example.mercader.data.remote.models.Difficulty("", ""),
                            editorial = com.example.mercader.data.remote.models.Editorial("", ""),
                            stock = 0,
                            price = (response.precioJuegos / response.cantidadSolicitada).toFloat()
                        ),
                        quantity = response.cantidadSolicitada
                    )
                }
                saveLocalCache(localItems)
                localItems
            } else {
                // Si falla, intentar cargar desde cache local
                getLocalCache()
            }
        } catch (e: Exception) {
            // Error, cargar desde cache local
            getLocalCache()
        }
    }

    // Actualizar cantidad (backend + cache)
    suspend fun updateQuantity(gameId: String, newQuantity: Int): Boolean {
        return try {
            val result = cartRepository.updateQuantity(gameId, newQuantity)
            if (result.isSuccess) {
                updateLocalCacheFromBackend()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Incrementar cantidad
    suspend fun incrementQuantity(gameId: String): Boolean {
        val currentCart = getCart()
        val currentItem = currentCart.find { it.game.id == gameId }
        val newQuantity = (currentItem?.quantity ?: 0) + 1
        return updateQuantity(gameId, newQuantity)
    }

    // Decrementar cantidad
    suspend fun decrementQuantity(gameId: String): Boolean {
        val currentCart = getCart()
        val currentItem = currentCart.find { it.game.id == gameId }
        val newQuantity = (currentItem?.quantity ?: 0) - 1

        return if (newQuantity <= 0) {
            removeFromCart(gameId)
        } else {
            updateQuantity(gameId, newQuantity)
        }
    }

    // Remover juego del carrito
    suspend fun removeFromCart(gameId: String): Boolean {
        println("🗑️ CartManager: removeFromCart iniciado para gameId: $gameId")

        return try {
            println("🗑️ CartManager: Llamando a cartRepository.removeFromCart")
            val result = cartRepository.removeFromCart(gameId)
            println("🗑️ CartManager: Result.isSuccess: ${result.isSuccess}")

            if (result.isSuccess) {
                println("🗑️ CartManager: Eliminación exitosa")
                updateLocalCacheFromBackend()
                true
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                println("🗑️ CartManager: Error en resultado: $error")
                false
            }
        } catch (e: Exception) {
            println("🗑️ CartManager: Excepción capturada: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    // Vaciar carrito
    suspend fun clearCart(): Boolean {
        val currentCart = getCart()
        var allRemoved = true

        for (item in currentCart) {
            val removed = removeFromCart(item.game.id)
            if (!removed) allRemoved = false
        }

        return allRemoved
    }

    // Obtener cantidad total de items
    suspend fun getTotalItemCount(): Int {
        val cart = getCart()
        return cart.sumOf { it.quantity }
    }

    // Obtener precio total
    suspend fun getTotalPrice(): Double {
        val cart = getCart()
        return cart.sumOf { (it.game.price * it.quantity).toDouble() }
    }

    // Verificar si un juego está en el carrito
    suspend fun isInCart(gameId: String): Boolean {
        val cart = getCart()
        return cart.any { it.game.id == gameId }
    }

    // Obtener cantidad de un juego específico
    suspend fun getQuantity(gameId: String): Int {
        val cart = getCart()
        return cart.find { it.game.id == gameId }?.quantity ?: 0
    }

    // Actualizar cache local desde backend
    private suspend fun updateLocalCacheFromBackend() {
        try {
            val cart = getCart() // Esto llama al backend y guarda en cache
        } catch (e: Exception) {
            // Ignorar error
        }
    }

    // Guardar cache local
    private fun saveLocalCache(cart: List<CartItem>) {
        val json = gson.toJson(cart)
        prefs.edit().putString(CART_KEY, json).apply()
    }

    // Obtener cache local
    private fun getLocalCache(): List<CartItem> {
        val json = prefs.getString(CART_KEY, "[]")
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun checkout(metodoPagoId: String): Boolean {
        return try {
            val result = cartRepository.checkout(metodoPagoId)

            if (result.isSuccess) {
                println("✅ CartManager: Compra exitosa, limpiando cache local")
                // Limpiar cache local después de compra exitosa
                clearLocalCache()
                true
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                println("❌ CartManager: Error en compra: $error")
                false
            }
        } catch (e: Exception) {
            println("❌ CartManager: Excepción en checkout: ${e.message}")
            false
        }
    }

    suspend fun checkoutWithReceipt(metodoPagoId: String, receiptFile: File): Boolean {
        return try {
            println("💳 CartManager: Procesando compra con comprobante")
            val result = cartRepository.checkoutWithReceipt(metodoPagoId, receiptFile)

            if (result.isSuccess) {
                println("✅ CartManager: Compra exitosa, limpiando cache local")
                clearLocalCache()
                true
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                println("❌ CartManager: Error en compra: $error")
                false
            }
        } catch (e: Exception) {
            println("❌ CartManager: Excepción en checkout: ${e.message}")
            false
        }
    }

    private fun clearLocalCache() {
        saveLocalCache(emptyList())
    }
}