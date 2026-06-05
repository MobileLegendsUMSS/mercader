package com.example.mercader.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.common.utils.AdminPurchaseManager
import com.example.mercader.data.remote.models.AdminPurchaseItemDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminPurchaseUiState(
    val purchases: List<AdminPurchaseItemDTO> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AdminPurchaseViewModel @Inject constructor(
    private val adminPurchaseManager: AdminPurchaseManager
) : ViewModel() {

    private val _state = MutableStateFlow(AdminPurchaseUiState())
    val state: StateFlow<AdminPurchaseUiState> = _state.asStateFlow()

    fun clearPurchases() {
        _state.value = _state.value.copy(
            purchases = emptyList(),
            isLoading = true,
            errorMessage = null
        )
    }

    fun loadPurchases() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val purchases = adminPurchaseManager.getAllPurchases()
                _state.value = _state.value.copy(
                    purchases = purchases,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar compras"
                )
            }
        }
    }

    fun updatePurchaseState(purchaseId: String, acceptance: String, onResult: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val success = adminPurchaseManager.updatePurchaseState(purchaseId, acceptance)
                if (success) {
                    loadPurchases() // Recargar lista después de actualizar
                }
                onResult(success)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}