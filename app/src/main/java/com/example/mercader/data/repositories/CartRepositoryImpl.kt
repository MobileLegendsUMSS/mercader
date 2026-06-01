package com.example.mercader.data.repository

import com.example.mercader.data.remote.apiservice.CartApiService
import com.example.mercader.data.remote.models.BuyRequest
import com.example.mercader.data.remote.models.CartRequest
import com.example.mercader.data.remote.models.DeleteFromCartRequest
import com.example.mercader.data.repositories.CartRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val apiService: CartApiService
) : CartRepository {

    override suspend fun addToCart(gameId: String, quantity: Int): Result<Unit> {
        return try {
            val request = CartRequest(
                idJuego = gameId,
                cantidad = quantity
            )
            val response = apiService.addToCart(request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al agregar al carrito"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error del servidor: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getCart(): Result<List<com.example.mercader.data.remote.models.CartItemResponse>> {
        return try {
            val response = apiService.getCart()

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: emptyList()
                Result.success(data)
            } else {
                val errorMessage = response.body()?.message ?: "Error al obtener el carrito"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error del servidor: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun updateQuantity(gameId: String, quantity: Int): Result<Unit> {
        return try {
            val request = CartRequest(
                idJuego = gameId,
                cantidad = quantity
            )
            val response = apiService.updateQuantity(request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al actualizar cantidad"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error del servidor: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun removeFromCart(gameId: String): Result<Unit> {
        return try {
            val request = DeleteFromCartRequest(
                idJuego = gameId
            )

            val response = apiService.removeFromCart(request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al eliminar del carrito"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error del servidor: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun checkout(paymentMethod: String): Result<Unit> {
        return try {
            val request = BuyRequest(
                idMetodoPago = paymentMethod
            )

            val response = apiService.checkout(request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al procesar la compra"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error del servidor: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}