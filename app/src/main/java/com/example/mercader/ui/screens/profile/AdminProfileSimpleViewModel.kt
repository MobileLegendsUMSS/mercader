package com.example.mercader.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.domain.repositories.UserRepository
import com.example.mercader.domain.usecases.AuthenticationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminProfileSimpleState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val username: String = "",
    val name: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val rol: String = ""
)

@HiltViewModel
class AdminProfileSimpleViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authUseCase: AuthenticationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminProfileSimpleState())
    val state: StateFlow<AdminProfileSimpleState> = _state.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _editedName = MutableStateFlow("")
    val editedName: StateFlow<String> = _editedName.asStateFlow()

    private val _editedLastName = MutableStateFlow("")
    val editedLastName: StateFlow<String> = _editedLastName.asStateFlow()

    private val _editedPhone = MutableStateFlow("")
    val editedPhone: StateFlow<String> = _editedPhone.asStateFlow()

    private val _editedEmail = MutableStateFlow("")
    val editedEmail: StateFlow<String> = _editedEmail.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        loadAdminProfile()
        loadUserRol()
    }

    private fun loadAdminProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = userRepository.getUserProfile("")
                result.fold(
                    onSuccess = { profile ->
                        _state.value = _state.value.copy(
                            username = profile.username,
                            name = profile.name,
                            lastName = profile.lastName,
                            phone = profile.phone,
                            email = profile.email,
                            isLoading = false,
                            errorMessage = null
                        )
                        _editedName.value = profile.name
                        _editedLastName.value = profile.lastName
                        _editedPhone.value = profile.phone
                        _editedEmail.value = profile.email
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar el perfil"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar el perfil"
                )
            }
        }
    }

    private fun loadUserRol() {
        viewModelScope.launch {
            val rol = authUseCase.getUserRol()
            _state.value = _state.value.copy(
                rol = when(rol?.lowercase()) {
                    "superadmin" -> "SUPER ADMIN"
                    "admin" -> "ADMINISTRADOR"
                    else -> "ADMIN"
                }
            )
        }
    }

    fun toggleEditMode() {
        if (_isEditMode.value) {
            _editedName.value = _state.value.name
            _editedLastName.value = _state.value.lastName
            _editedPhone.value = _state.value.phone
            _editedEmail.value = _state.value.email
        }
        _isEditMode.value = !_isEditMode.value
    }

    fun updateEditedName(value: String) {
        _editedName.value = value
    }

    fun updateEditedLastName(value: String) {
        _editedLastName.value = value
    }

    fun updateEditedPhone(value: String) {
        _editedPhone.value = value
    }

    fun updateEditedEmail(value: String) {
        _editedEmail.value = value
    }

    fun saveProfileChanges() {
        viewModelScope.launch {
            _isSaving.value = true
            _state.value = _state.value.copy(errorMessage = null)

            val updatedFields = mutableMapOf<String, Any>()

            if (_editedName.value != _state.value.name) {
                updatedFields["nombres"] = _editedName.value
            }
            if (_editedLastName.value != _state.value.lastName) {
                updatedFields["apellidos"] = _editedLastName.value
            }
            if (_editedPhone.value != _state.value.phone) {
                updatedFields["telefono"] = _editedPhone.value
            }
            if (_editedEmail.value != _state.value.email) {
                updatedFields["correo_contacto"] = _editedEmail.value
            }

            if (updatedFields.isEmpty()) {
                _isEditMode.value = false
                _isSaving.value = false
                return@launch
            }

            try {
                val result = userRepository.editProfile(updatedFields)
                result.fold(
                    onSuccess = {
                        // Actualizar estado con los nuevos valores
                        _state.value = _state.value.copy(
                            name = _editedName.value,
                            lastName = _editedLastName.value,
                            phone = _editedPhone.value,
                            email = _editedEmail.value
                        )
                        _isEditMode.value = false
                        _isSaving.value = false
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(
                            errorMessage = error.message ?: "Error al actualizar el perfil"
                        )
                        _isSaving.value = false
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = e.message ?: "Error al actualizar el perfil"
                )
                _isSaving.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
        }
    }
}