package com.example.mercader.domain.models

data class Review(
    val id: String,
    val userId: String,
    val userName: String,
    val content: String,
    val timestamp: Long  // Unix timestamp
)