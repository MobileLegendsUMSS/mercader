package com.example.mercader.data.remote.apiservice

import com.example.mercader.data.remote.models.report.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

interface ReportApiService {

    @POST("/api/reportes/admin/juegos-stock")
    suspend fun getJuegosStock(
        @Body request: ReportStockRequest
    ): Response<ReportStockResponse>
    @GET("/api/reportes/admin/categorias-populares")
    suspend fun getCategoriasPopulares(): Response<CategoriasPopularesResponse>

    @POST("/api/reportes/superadmin/ingresos-periodo")
    suspend fun getIngresosPeriodo(
        @Body request: ReportPeriodoRequest
    ): Response<IngresosPeriodoResponse>

    @POST("/api/reportes/superadmin/usos-periodo")
    suspend fun getUsosPeriodo(
        @Body request: ReportPeriodoRequest
    ): Response<UsosPeriodoResponse>
}