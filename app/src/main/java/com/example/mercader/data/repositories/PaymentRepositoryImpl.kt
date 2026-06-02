package com.example.mercader.data.repositories

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.mercader.data.remote.apiservice.ConfirmPaymentRequest
import com.example.mercader.domain.repositories.PaymentRepository
import com.example.mercader.data.remote.apiservice.PaymentApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val apiService: PaymentApiService,
    private val contentResolver: ContentResolver
) : PaymentRepository {

    companion object {
        private const val TAG = "PaymentRepository"
    }

    override suspend fun uploadReceipt(
        imageFile: File,
        onProgress: (Int) -> Unit
    ): Result<String> {
        return try {
            Log.d(TAG, "Iniciando subida de archivo: ${imageFile.name}, tamaño: ${imageFile.length()} bytes")

            // Crear el RequestBody desde el archivo
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())

            // Crear el MultipartBody.Part con el archivo
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            // Subir el archivo
            val response = apiService.uploadReceipt(body)

            Log.d(TAG, "Respuesta recibida - Código: ${response.code()}, exitoso: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val bodyResponse = response.body()
                Log.d(TAG, "Body response: $bodyResponse")

                if (bodyResponse != null) {
                    val imageUrl = bodyResponse.imageUrl
                    Log.d(TAG, "URL de imagen recibida: $imageUrl")
                    Result.success(imageUrl)
                } else {
                    Log.e(TAG, "Body de respuesta es null")
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Error en subida - Código: ${response.code()}, Mensaje: ${response.message()}, Body: $errorBody")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción durante la subida", e)
            Result.failure(e)
        }
    }

    override suspend fun uploadReceiptFromUri(
        uri: Uri,
        onProgress: (Int) -> Unit
    ): Result<String> {
        return try {
            Log.d(TAG, "URI recibida: $uri")

            // Primero convertir URI a File
            val file = createFileFromUri(uri)
            if (file == null) {
                Log.e(TAG, "No se pudo crear el archivo desde la URI")
                return Result.failure(Exception("No se pudo crear el archivo desde la URI"))
            }

            Log.d(TAG, "Archivo creado temporalmente: ${file.absolutePath}, tamaño: ${file.length()}")

            // Luego subir el archivo
            val result = uploadReceipt(file, onProgress)

            // Limpiar archivo temporal después de subir
            try {
                file.delete()
                Log.d(TAG, "Archivo temporal eliminado")
            } catch (e: Exception) {
                Log.w(TAG, "Error al eliminar archivo temporal", e)
            }

            result
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al procesar URI", e)
            Result.failure(e)
        }
    }

    override suspend fun getQrCodeBitmap(): Bitmap? {
        return try {
            val response = apiService.getQrCode()
            if (response.isSuccessful && response.body() != null) {
                val qrData = response.body()?.qrData ?: return null
                decodeQrDataToBitmap(qrData)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun verifyPayment(purchaseId: String): Result<Boolean> {
        return try {
            val response = apiService.verifyPayment(purchaseId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()?.isVerified ?: false)
            } else {
                Result.failure(Exception(response.message() ?: "Error al verificar el pago"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmPayment(
        purchaseId: String,
        receiptImageUrl: String,
        amount: Double
    ): Result<Unit> {
        return try {
            val request = ConfirmPaymentRequest(purchaseId, receiptImageUrl, amount)
            val response = apiService.confirmPayment(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Error al confirmar el pago"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createFileFromUri(uri: Uri): File? {
        return try {
            Log.d(TAG, "Creando archivo desde URI: $uri")
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e(TAG, "No se pudo abrir InputStream para URI")
                return null
            }

            val tempFile = File.createTempFile("receipt_", ".jpg")
            Log.d(TAG, "Archivo temporal creado: ${tempFile.absolutePath}")

            FileOutputStream(tempFile).use { outputStream ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytes = 0L
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytes += bytesRead
                }
                Log.d(TAG, "Archivo escrito, tamaño total: $totalBytes bytes")
            }
            inputStream.close()
            tempFile
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear archivo desde URI", e)
            null
        }
    }

    private fun decodeQrDataToBitmap(qrData: String): Bitmap? {
        return try {
            val decodedBytes = android.util.Base64.decode(qrData, android.util.Base64.DEFAULT)
            decodedBytes?.takeIf { it.isNotEmpty() }
                ?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        } catch (e: Exception) {
            null
        }
    }
}