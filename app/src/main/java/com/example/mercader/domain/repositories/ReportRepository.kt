package com.example.mercader.data.repository

import com.example.mercader.data.remote.models.report.*

interface ReportRepository {
    suspend fun getJuegosStock(allGames: Boolean, order: String, amount: Int? = null): Result<ReportStockResponse>
    suspend fun getCategoriasPopulares(): Result<CategoriasPopularesResponse>
    suspend fun getIngresosPeriodo(timePeriod: String, timeValue: String): Result<IngresosPeriodoResponse>
    suspend fun getUsosPeriodo(timePeriod: String, timeValue: String): Result<UsosPeriodoResponse>
}