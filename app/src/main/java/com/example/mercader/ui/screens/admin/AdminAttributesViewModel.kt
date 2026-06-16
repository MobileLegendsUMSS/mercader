package com.example.mercader.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Editorial
import com.example.mercader.domain.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminAttributesState(
    val categories: List<Category> = emptyList(),
    val editorials: List<Editorial> = emptyList(),
    val isLoadingCategories: Boolean = false,
    val isLoadingEditorials: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdminAttributesViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminAttributesState())
    val state: StateFlow<AdminAttributesState> = _state.asStateFlow()

    init {
        loadAttributes()
    }

    fun loadAttributes() {
        loadCategories()
        loadEditorials()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCategories = true, error = null) }
            val result = gameRepository.getGameTypes()
            if (result.isSuccess) {
                _state.update { it.copy(categories = result.getOrNull() ?: emptyList(), isLoadingCategories = false) }
            } else {
                _state.update { it.copy(error = result.exceptionOrNull()?.message, isLoadingCategories = false) }
            }
        }
    }

    private fun loadEditorials() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingEditorials = true, error = null) }
            val result = gameRepository.getEditorials()
            if (result.isSuccess) {
                _state.update { it.copy(editorials = result.getOrNull() ?: emptyList(), isLoadingEditorials = false) }
            } else {
                _state.update { it.copy(error = result.exceptionOrNull()?.message, isLoadingEditorials = false) }
            }
        }
    }

    fun createCategory(descripcion: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = gameRepository.createCategory(descripcion)
            if (result.isSuccess) {
                loadCategories()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al crear categoría")
            }
        }
    }

    fun updateCategory(id: String, descripcion: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = gameRepository.updateCategory(id, descripcion)
            if (result.isSuccess) {
                loadCategories()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al actualizar categoría")
            }
        }
    }

    fun deleteCategory(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = gameRepository.deleteCategory(id)
            if (result.isSuccess) {
                loadCategories()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al eliminar categoría")
            }
        }
    }

    fun createEditorial(nombre: String, pais: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = gameRepository.createEditorial(nombre, pais)
            if (result.isSuccess) {
                loadEditorials()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al crear editorial")
            }
        }
    }

    fun updateEditorial(id: String, nombre: String, pais: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = gameRepository.updateEditorial(id, nombre, pais)
            if (result.isSuccess) {
                loadEditorials()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al actualizar editorial")
            }
        }
    }

    fun deleteEditorial(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = gameRepository.deleteEditorial(id)
            if (result.isSuccess) {
                loadEditorials()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al eliminar editorial")
            }
        }
    }
}
