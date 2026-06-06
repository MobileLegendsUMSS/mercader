package com.example.mercader.ui.screens.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.data.remote.apiservice.AdminApiService
import com.example.mercader.data.remote.apiservice.ChangeRoleRequest
import com.example.mercader.ui.screens.admin.model.AdminRoleUiState
import com.example.mercader.ui.screens.admin.model.UserRoleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminRoleViewModel @Inject constructor(
    private val adminApiService: AdminApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminRoleUiState())
    val uiState: StateFlow<AdminRoleUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _updatingUsers = MutableStateFlow<Set<String>>(emptySet())

    fun isUpdating(userId: String): Boolean = _updatingUsers.value.contains(userId)

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = adminApiService.getUsuariosConRoles()
                if (response.isSuccessful && response.body()?.success == true) {
                    val users = response.body()?.data?.map { dto ->
                        UserRoleItem(
                            id = dto._id,
                            nombre = dto.nombre,
                            nombres = dto.nombres,
                            apellidos = dto.apellidos,
                            telefono = dto.telefono,
                            correo_contacto = dto.correo_contacto,
                            mercapoints = dto.mercapoints,
                            rol = dto.rol
                        )
                    } ?: emptyList()
                    _uiState.value = _uiState.value.copy(users = users, errorMessage = null)
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = response.body()?.message ?: "Error al cargar usuarios"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error de conexión"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changeRole(userId: String, newRole: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _updatingUsers.value = _updatingUsers.value + userId
            try {
                val response = adminApiService.cambiarRol(
                    ChangeRoleRequest(
                        id_usuario = userId,
                        nuevo_rol = newRole
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    // Actualizar usuario en la lista
                    val updatedUsers = _uiState.value.users.map { user ->
                        if (user.id == userId) {
                            user.copy(rol = newRole)
                        } else {
                            user
                        }
                    }
                    _uiState.value = _uiState.value.copy(
                        users = updatedUsers,
                        errorMessage = null
                    )
                    onComplete(true)
                } else {
                    val error = response.body()?.message ?: "Error al cambiar rol"
                    _uiState.value = _uiState.value.copy(errorMessage = error)
                    onComplete(false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error de conexión"
                )
                onComplete(false)
            } finally {
                _updatingUsers.value = _updatingUsers.value - userId
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}