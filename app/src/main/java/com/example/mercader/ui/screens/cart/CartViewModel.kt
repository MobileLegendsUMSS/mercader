package com.example.mercader.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.common.utils.CartManager
import com.example.mercader.domain.models.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartManager: CartManager
) : ViewModel() {

    private val _state = MutableStateFlow(CartUiState())
    val state: StateFlow<CartUiState> = _state.asStateFlow()

    data class CartUiState(
        val cartItems: List<CartItem> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val totalItems: Int = 0,
        val totalPrice: Double = 0.0
    )

    companion object {
        private const val HARDCODED_PAYMENT_METHOD = "6a0be36f16b8981d137c9595"
    }

    fun loadCart() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val cartItems = cartManager.getCart()
                val totalItems = cartManager.getTotalItemCount()
                val totalPrice = cartManager.getTotalPrice()

                _state.value = _state.value.copy(
                    cartItems = cartItems,
                    isLoading = false,
                    totalItems = totalItems,
                    totalPrice = totalPrice,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar el carrito"
                )
            }
        }
    }

    fun incrementQuantity(gameId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val success = cartManager.incrementQuantity(gameId)
                if (success) {
                    loadCart() // Recargar para obtener valores actualizados
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo aumentar la cantidad"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al actualizar cantidad"
                )
            }
        }
    }

    fun decrementQuantity(gameId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val success = cartManager.decrementQuantity(gameId)
                if (success) {
                    loadCart()
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo disminuir la cantidad"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al actualizar cantidad"
                )
            }
        }
    }

    fun removeFromCart(gameId: String) {
        println("🗑️ CartViewModel: removeFromCart llamado para gameId: $gameId")
        viewModelScope.launch {
            println("🗑️ CartViewModel: Corrutina iniciada")
            _state.value = _state.value.copy(isLoading = true)

            try {
                val success = cartManager.removeFromCart(gameId)
                println("🗑️ CartViewModel: Resultado de removeFromCart: $success")
                if (success) {
                    println("🗑️ CartViewModel: Éxito, recargando carrito")
                    loadCart()
                } else {
                    println("🗑️ CartViewModel: Fracaso, mostrando error")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo eliminar el juego del carrito"
                    )
                }
            } catch (e: Exception) {
                println("🗑️ CartViewModel: Excepción: ${e.message}")
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al eliminar del carrito"
                )
            }
        }
    }

    fun processCheckout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                println("CartViewModel: Procesando checkout")
                val success = cartManager.checkout(HARDCODED_PAYMENT_METHOD)

                if (success) {
                    println("CartViewModel: Checkout exitoso")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        cartItems = emptyList(),
                        totalItems = 0,
                        totalPrice = 0.0
                    )
                    onSuccess()
                } else {
                    println("❌ CartViewModel: Checkout fallido")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo completar la compra. Verifica tu conexión o intenta más tarde."
                    )
                }
            } catch (e: Exception) {
                println("CartViewModel: Excepción en checkout: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al procesar la compra"
                )
            }
        }
    }

    fun processCheckoutWithReceipt(receiptFile: File, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                println("💳 CartViewModel: Procesando checkout con comprobante")
                val success = cartManager.checkoutWithReceipt(HARDCODED_PAYMENT_METHOD, receiptFile)

                if (success) {
                    println("✅ CartViewModel: Checkout exitoso")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        cartItems = emptyList(),
                        totalItems = 0,
                        totalPrice = 0.0
                    )
                    onSuccess()
                } else {
                    println("❌ CartViewModel: Checkout fallido")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo completar la compra. Verifica tu conexión o intenta más tarde."
                    )
                }
            } catch (e: Exception) {
                println("❌ CartViewModel: Excepción en checkout: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al procesar la compra"
                )
            }
        }
    }
}