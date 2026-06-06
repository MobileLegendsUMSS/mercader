package com.example.mercader.ui.screens.admin.model

data class UserRoleItem(
    val id: String,
    val nombre: String,
    val nombres: String? = null,
    val apellidos: String? = null,
    val telefono: String,
    val correo_contacto: String,
    val mercapoints: Int,
    val rol: String
)

data class AdminRoleUiState(
    val users: List<UserRoleItem> = emptyList(),
    val errorMessage: String? = null
) {
    fun isEmpty(): Boolean = users.isEmpty()
}