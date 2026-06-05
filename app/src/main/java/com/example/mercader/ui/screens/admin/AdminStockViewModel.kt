// ui/screens/admin/AdminStockViewModel.kt
package com.example.mercader.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.domain.usecases.DeleteGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminStockViewModel @Inject constructor(
    private val deleteGameUseCase: DeleteGameUseCase
) : ViewModel() {

    fun deleteGame(gameId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = deleteGameUseCase.deleteGame(gameId, "Eliminado por administrador")
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    onError(result.exceptionOrNull()?.message ?: "Error al eliminar")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            }
        }
    }
}