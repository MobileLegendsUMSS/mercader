package com.example.mercader.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.domain.models.UserLoan
import com.example.mercader.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminLoanUiState(
    val loans: List<UserLoan> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AdminLoanViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminLoanUiState())
    val state: StateFlow<AdminLoanUiState> = _state.asStateFlow()

    fun loadLoans(vigente: Boolean, recogido: Boolean, devuelto: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = userRepository.getAllLoans(vigente, recogido, devuelto)
                if (result.isSuccess) {
                    _state.value = _state.value.copy(
                        loans = result.getOrNull() ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar préstamos"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun updateLoan(
        loanId: String,
        fechaInicio: String?,
        fechaFin: String?,
        onResult: (success: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = userRepository.updateLoan(loanId, fechaInicio, fechaFin)
                if (result.isSuccess) {
                    onResult(true)
                } else {
                    _state.value = _state.value.copy(
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar"
                    )
                    onResult(false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message)
                onResult(false)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}