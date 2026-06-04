package com.example.mercader.data.remote.models.report

data class ReportStockResponse(
    val success: Boolean,
    val message: String,
    val data: List<JuegoStock>
)

data class JuegoStock(
    val _id: String,
    val titulo: String,
    val descripcion: String,
    val cantidad: Int,
    val cantidad_prestamo: Int,
    val disponible: Boolean,
    val activo: Boolean
)