package com.example.mercader.data.remote.models.report


data class ReportStockRequest(
    val allGames: Boolean,
    val order: String, // "ascendente" o "descendente"
    val amount: Int? = null
)