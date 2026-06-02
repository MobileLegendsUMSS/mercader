package com.example.mercader.data.remote.apiservice

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PaymentApiService {

    @Multipart
    @POST("api/payments/upload-receipt")
    suspend fun uploadReceipt(
        @Part file: MultipartBody.Part
    ): Response<UploadReceiptResponse>

    @GET("api/payments/qr-code")
    suspend fun getQrCode(): Response<QrCodeResponse>

    @POST("api/payments/verify/{purchaseId}")
    suspend fun verifyPayment(
        @Path("purchaseId") purchaseId: String
    ): Response<VerifyPaymentResponse>

    @POST("api/payments/confirm")
    suspend fun confirmPayment(
        @Body request: ConfirmPaymentRequest
    ): Response<ConfirmPaymentResponse>
}

// Si el backend devuelve una estructura diferente, ajústala aquí
data class UploadReceiptResponse(
    val success: Boolean,
    val imageUrl: String,
    val message: String? = null
)

data class QrCodeResponse(
    val success: Boolean,
    val qrData: String,
    val message: String? = null
)

data class VerifyPaymentResponse(
    val success: Boolean,
    val isVerified: Boolean,
    val message: String? = null
)

data class ConfirmPaymentRequest(
    val purchaseId: String,
    val receiptImageUrl: String,
    val amount: Double
)

data class ConfirmPaymentResponse(
    val success: Boolean,
    val message: String? = null
)