package com.example.mercader.domain.repositories

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface PaymentRepository {
    suspend fun uploadReceipt(
        imageFile: File,
        onProgress: (Int) -> Unit = {}
    ): Result<String>

    suspend fun uploadReceiptFromUri(
        uri: Uri,
        onProgress: (Int) -> Unit = {}
    ): Result<String>

    suspend fun getQrCodeBitmap(): Bitmap?

    suspend fun verifyPayment(purchaseId: String): Result<Boolean>

    suspend fun confirmPayment(
        purchaseId: String,
        receiptImageUrl: String,
        amount: Double
    ): Result<Unit>
}