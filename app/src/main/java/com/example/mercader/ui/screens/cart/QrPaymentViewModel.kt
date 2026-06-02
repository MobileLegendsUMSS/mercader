package com.example.mercader.ui.screens.cart

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.domain.repositories.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QrPaymentState(
    val isUploading: Boolean = false,
    val receiptImageUrl: String? = null,
    val errorMessage: String? = null,
    val uploadProgress: Int = 0
)

@HiltViewModel
class QrPaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    companion object {
        private const val TAG = "QrPaymentViewModel"
    }

    private val _state = MutableStateFlow(QrPaymentState())
    val state: StateFlow<QrPaymentState> = _state.asStateFlow()

    fun uploadReceipt(uri: Uri, purchaseId: String) {
        Log.d(TAG, "uploadReceipt llamado con URI: $uri, purchaseId: $purchaseId")

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isUploading = true,
                errorMessage = null,
                uploadProgress = 0
            )

            try {
                Log.d(TAG, "Iniciando subida de comprobante")
                val result = paymentRepository.uploadReceiptFromUri(uri) { progress ->
                    Log.d(TAG, "Progreso de subida: $progress%")
                    _state.value = _state.value.copy(uploadProgress = progress)
                }

                result.fold(
                    onSuccess = { imageUrl ->
                        Log.d(TAG, "Subida exitosa, URL recibida: $imageUrl")
                        _state.value = _state.value.copy(
                            isUploading = false,
                            receiptImageUrl = imageUrl,
                            uploadProgress = 100
                        )
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Error en subida", error)
                        _state.value = _state.value.copy(
                            isUploading = false,
                            errorMessage = error.message ?: "Error al subir el comprobante"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en uploadReceipt", e)
                _state.value = _state.value.copy(
                    isUploading = false,
                    errorMessage = e.message ?: "Error al subir el comprobante"
                )
            }
        }
    }

    fun confirmPayment(purchaseId: String, amount: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val receiptUrl = _state.value.receiptImageUrl
            if (receiptUrl == null) {
                _state.value = _state.value.copy(
                    errorMessage = "No hay comprobante para confirmar"
                )
                return@launch
            }

            _state.value = _state.value.copy(isUploading = true, errorMessage = null)

            val result = paymentRepository.confirmPayment(purchaseId, receiptUrl, amount)
            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(isUploading = false)
                    onSuccess()
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isUploading = false,
                        errorMessage = error.message ?: "Error al confirmar el pago"
                    )
                }
            )
        }
    }

    fun downloadQrCode() {
        viewModelScope.launch {
            try {
                val bitmap = paymentRepository.getQrCodeBitmap()
                if (bitmap != null) {
                    _state.value = _state.value.copy(errorMessage = null)
                } else {
                    _state.value = _state.value.copy(
                        errorMessage = "Error al generar el código QR"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = e.message ?: "Error al descargar el QR"
                )
            }
        }
    }

    fun clearReceipt() {
        _state.value = _state.value.copy(receiptImageUrl = null)
    }

    fun setErrorMessage(message: String) {
        _state.value = _state.value.copy(errorMessage = message)
    }
}