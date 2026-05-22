package com.example.mercader.ui.screens.profile

data class ProfileState(
    val username: String = "",
    val name: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val mercaPoints: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
