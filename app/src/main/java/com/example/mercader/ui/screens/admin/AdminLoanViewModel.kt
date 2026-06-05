package com.example.mercader.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.domain.models.UserLoan
import com.example.mercader.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun loadLoans(vigente: Boolean, recogido: Boolean, devuelto: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = userRepository.getAllLoans(vigente, recogido, devuelto)
                if (result.isSuccess) {
                    var loans = result.getOrNull() ?: emptyList()

                    loans = loans.filter { loan ->
                        when {
                            devuelto -> loan.endDate != null
                            recogido -> loan.startDate != null && loan.endDate == null
                            vigente -> loan.startDate == null && loan.endDate == null
                            else -> true
                        }
                    }

                    _state.value = _state.value.copy(
                        loans = loans,
                        isLoading = false,
                        errorMessage = null
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
                    _toastMessage.emit("Préstamo actualizado correctamente")
                    onResult(true)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _toastMessage.emit("Error: $error")
                    onResult(false)
                }
            } catch (e: Exception) {
                _toastMessage.emit("Error: ${e.message}")
                onResult(false)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun clearLoans() {
        _state.value = _state.value.copy(
            loans = emptyList(),
            isLoading = true,
            errorMessage = null
        )
    }
}