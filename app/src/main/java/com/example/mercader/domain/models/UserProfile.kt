package com.example.mercader.domain.models

data class UserProfile(
    val id: String = "",
    val username: String = "",
    val name: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val mercaPoints: Int = 0
)
