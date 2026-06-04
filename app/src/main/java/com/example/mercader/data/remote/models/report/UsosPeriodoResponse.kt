package com.example.mercader.data.remote.models.report

data class UsosPeriodoResponse(
    val success: Boolean,
    val message: String,
    val data: UsosData?
)

data class UsosData(
    val prestamos_totales: Int,
    val prestamos: Int,
    val alquileres: Int
)