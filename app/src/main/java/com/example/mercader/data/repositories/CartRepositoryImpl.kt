package com.example.mercader.data.repository

import com.example.mercader.data.remote.apiservice.CartApiService
import com.example.mercader.data.remote.models.BuyRequest
import com.example.mercader.data.remote.models.CartRequest
import com.example.mercader.data.remote.models.DeleteFromCartRequest
import com.example.mercader.data.repositories.CartRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import retrofit2.http.PartMap

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

    override suspend fun checkoutWithReceipt(
        metodoPagoId: String,
        receiptFile: File
    ): Result<Unit> {
        return try {
            println("💳 CartRepository: checkoutWithReceipt - metodoPagoId: $metodoPagoId")
            println("💳 Archivo: ${receiptFile.name}, tamaño: ${receiptFile.length()} bytes")

            val fields = mutableMapOf<String, RequestBody>()
            fields["id_metodo_pago"] = metodoPagoId.toRequestBody("text/plain".toMediaTypeOrNull())

            // Crear el archivo con MIME type correcto
            val mimeType = when (receiptFile.extension.lowercase()) {
                "png" -> "image/png"
                "jpg", "jpeg" -> "image/jpeg"
                else -> "image/jpeg"
            }
            val requestFile = receiptFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val comprobantePart = MultipartBody.Part.createFormData(
                "comprobante",
                receiptFile.name,
                requestFile
            )

            val response = apiService.checkoutWithReceipt(fields, comprobantePart)

            println("💳 Response code: ${response.code()}")
            println("💳 Response successful: ${response.isSuccessful}")
            println("💳 Response body: ${response.body()}")

            if (response.isSuccessful && response.body()?.success == true) {
                println("✅ Compra realizada exitosamente con comprobante")
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al procesar la compra"
                println("❌ Error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            println("❌ Error de red: ${e.message}")
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            println("❌ Error HTTP: ${e.message}")
            Result.failure(Exception("Error del servidor: ${e.message}"))
        } catch (e: Exception) {
            println("❌ Error inesperado: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}