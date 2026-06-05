package com.example.mercader.common.utils

import com.example.mercader.domain.repositories.ReserveRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReserveManager @Inject constructor(
    private val reserveRepository: ReserveRepository
) {

    companion object {

        // Fecha
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    // Obtener fecha actual en formato ISO 8601 para Bolivia
    fun getCurrentDateTime(): String {
        val now = Date()

        // Ajustar a la hora del servidor
        val calendar = Calendar.getInstance().apply {
            time = now
            add(Calendar.HOUR_OF_DAY, -4)
        }

        return dateFormat.format(calendar.time)
    }

    // Solicitar préstamo
    suspend fun bookReserve(
        gameId: String,
        tipoServicio: String
    ): Result<Unit> {
        val fechaActual = getCurrentDateTime()

        println("ReserveManager: Solicitando $tipoServicio")
        println("Fecha actual: $fechaActual")

        return try {
            val result = reserveRepository.registerReserve(
                gameId = gameId,
                serviceType = tipoServicio,
                reserveDate = fechaActual
            )
            result
        } catch (e: Exception) {
            println("❌ PrestamoManager: Error - ${e.message}")
            Result.failure(e)
        }
    }
}