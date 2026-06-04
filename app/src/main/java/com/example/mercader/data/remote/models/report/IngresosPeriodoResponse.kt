package com.example.mercader.data.remote.models.report

data class IngresosPeriodoResponse(
    val success: Boolean,
    val message: String,
    val data: IngresosData?
)

data class IngresosData(
    val ganancia: Double
)