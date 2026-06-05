package com.example.mercader.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.common.utils.AdminPurchaseManager
import com.example.mercader.data.remote.models.AdminPurchaseDetailDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminPurchaseDetailUiState(
    val purchaseDetail: AdminPurchaseDetailDTO? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AdminPurchaseDetailViewModel @Inject constructor(
    private val adminPurchaseManager: AdminPurchaseManager
) : ViewModel() {

    private val _state = MutableStateFlow(AdminPurchaseDetailUiState())
    val state: StateFlow<AdminPurchaseDetailUiState> = _state.asStateFlow()

    fun loadPurchaseDetail(purchaseId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val detail = adminPurchaseManager.getPurchaseById(purchaseId)
                _state.value = _state.value.copy(
                    purchaseDetail = detail,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar detalle"
                )
            }
        }
    }
}