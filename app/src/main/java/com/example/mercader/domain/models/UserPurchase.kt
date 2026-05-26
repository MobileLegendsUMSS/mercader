package com.example.mercader.domain.models

data class UserPurchase(
    val total: Double,
    val paymentMethod: String,
    val details: List<UserPurchaseDetail>
)

data class UserPurchaseDetail(
    val gameId: String,
    val title: String,
    val quantity: Int,
    val priceSubtotal: Double
)
