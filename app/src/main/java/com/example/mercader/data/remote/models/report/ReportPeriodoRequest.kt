package com.example.mercader.data.remote.models.report

data class ReportPeriodoRequest(
    val timePeriod: String, // "dia", "mes", "anio"
    val timeValue: String   // formato según el período
)