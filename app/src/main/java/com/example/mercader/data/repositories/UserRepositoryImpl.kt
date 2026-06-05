package com.example.mercader.data.repositories

import com.example.mercader.data.remote.apiservice.UserApiService
import com.example.mercader.data.remote.models.EditProfileRequestDTO
import com.example.mercader.data.remote.models.UpdateLoanRequestDTO
import com.example.mercader.data.remote.models.UserLoansRequestDTO
import com.example.mercader.domain.models.UserProfile
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.models.UserPurchase
import com.example.mercader.domain.models.UserPurchaseDetail
import com.example.mercader.domain.models.UserLoan
import com.example.mercader.domain.models.TopGame
import com.example.mercader.domain.repositories.UserRepository
import javax.inject.Inject

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import javax.net.ssl.SSLSocketFactory

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService,
    private val tokenRepository: com.example.mercader.data.local.ITokenRepository
) : UserRepository {

    override suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val response = userApiService.getUserProfile()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.result && body.data != null) {
                    val dto = body.data
                    Result.success(
                        UserProfile(
                            id = userId,
                            username = dto.username ?: "",
                            name = dto.name ?: "",
                            lastName = dto.lastName ?: "",
                            phone = dto.phone ?: "",
                            email = dto.email ?: "",
                            mercaPoints = dto.mercaPoints ?: 0
                        )
                    )
                } else {
                    Result.failure(Exception(body?.message ?: "Respuesta vacía del servidor"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkFavorite(gameId: String): Result<Boolean> {
        return try {
            val result = getFavorites()
            if (result.isSuccess) {
                val favorites = result.getOrNull() ?: emptyList()
                val isFavorite = favorites.any { it.id == gameId }

                Result.success(isFavorite)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Error al obtener favoritos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addFavorite(gameId: String): Result<Unit> {
        return try {
            val response = userApiService.addFavorite(gameId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al agregar a favoritos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFavorite(gameId: String): Result<Unit> {
        return try {
            val response = userApiService.removeFavorite(gameId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al eliminar de favoritos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFavorites(): Result<List<Game>> {
        return try {
            val response = userApiService.getFavorites()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val games = body.data?.map {
                        Game(
                            id = it.id_juego ?: "",
                            title = it.titlo ?: "",
                            description = it.descripcion ?: "",
                            tutorial = "",
                            category = com.example.mercader.data.remote.models.Category("", ""),
                            nMinPerson = it.cantMinPers ?: 0,
                            nMaxPerson = it.cantMaxPers ?: 0,
                            minMinutes = it.duracionMin ?: 0,
                            maxMinutes = it.duracionMax ?: 0,
                            difficulty = com.example.mercader.data.remote.models.Difficulty("", ""),
                            editorial = com.example.mercader.data.remote.models.Editorial("", ""),
                            stock = if (it.disponible == true) 1 else 0,
                            price = it.precio ?: 0f

                        )
                    } ?: emptyList()

                    Result.success(games)

                } else {
                    Result.failure(Exception(body?.message ?: "Error al obtener favoritos"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserPurchases(): Result<List<UserPurchase>> {
        return try {
            val response = userApiService.getUserPurchases()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val purchases = body.data?.map { item ->
                        UserPurchase(
                            total = item.total,
                            paymentMethod = item.paymentMethod,
                            details = item.details.map { detail ->
                                UserPurchaseDetail(
                                    gameId = detail.gameId,
                                    title = detail.title,
                                    quantity = detail.quantity,
                                    priceSubtotal = detail.priceSubtotal
                                )
                            }
                        )
                    } ?: emptyList()
                    Result.success(purchases)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al obtener compras"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserLoans(): Result<List<UserLoan>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenRepository.getAccessToken() ?: ""
                val host = "mercader-server.onrender.com"
                val path = "/api/servicios/usuarios/prestamos"
                val jsonBody = """{"vigent":true,"collected":false,"returned":false}"""

                val factory = SSLSocketFactory.getDefault()
                val socket = factory.createSocket(host, 443)

                val writer = PrintWriter(OutputStreamWriter(socket.getOutputStream(), "UTF-8"))
                writer.print("GET $path HTTP/1.0\r\n")
                writer.print("Host: $host\r\n")
                writer.print("Authorization: Bearer $token\r\n")
                writer.print("Content-Type: application/json\r\n")
                writer.print("Content-Length: ${jsonBody.length}\r\n")
                writer.print("\r\n")
                writer.print(jsonBody)
                writer.flush()

                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), "UTF-8"))
                var line = reader.readLine()

                var statusCode = 500
                if (line != null && line.startsWith("HTTP/1.")) {
                    val parts = line.split(" ")
                    if (parts.size >= 2) {
                        statusCode = parts[1].toIntOrNull() ?: 500
                    }
                }

                while (line != null && line.isNotEmpty()) {
                    line = reader.readLine()
                }

                val bodyBuilder = StringBuilder()
                while (true) {
                    line = reader.readLine()
                    if (line == null) break
                    bodyBuilder.append(line)
                }
                
                socket.close()
                val responseBody = bodyBuilder.toString()

                if (statusCode in 200..299) {
                    val parsed = Gson().fromJson(responseBody, com.example.mercader.data.remote.models.UserLoansListResponseDTO::class.java)
                    if (parsed.success) {
                        val loans = parsed.data?.map { item ->
                            UserLoan(
                                loanId = item.loanId,
                                title = item.title,
                                description = item.description ?: "",
                                service = item.service,
                                requestDate = item.requestDate,
                                limitDate = item.limitDate,
                                startDate = item.startDate,
                                endDate = item.endDate
                            )
                        }?.filter { it.endDate == null } ?: emptyList()
                        Result.success(loans)
                    } else {
                        Result.success(emptyList())
                    }
                } else {
                    Result.failure(Exception("Error $statusCode: $responseBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    override suspend fun editProfile(updatedFields: Map<String, Any>): Result<Unit> {
        return try {
            val request = EditProfileRequestDTO(
                nombres = updatedFields["nombres"] as? String,
                apellidos = updatedFields["apellidos"] as? String,
                telefono = updatedFields["telefono"] as? String,
                correoContacto = updatedFields["correo_contacto"] as? String
            )

            val response = userApiService.editPersonalInfo(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al actualizar el perfil"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTopGames(): Result<List<TopGame>> {
        return try {
            val response = userApiService.getTopGames()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val topGames = body.data.map { dto ->
                        TopGame(
                            title = dto.title,
                            loanCount = dto.loanCount
                        )
                    }
                    Result.success(topGames)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllLoans(
        vigente: Boolean,
        recogido: Boolean,
        devuelto: Boolean
    ): Result<List<UserLoan>> {
        return try {
            println("📋 Admin: Obteniendo todos los préstamos - vigente:$vigente, recogido:$recogido, devuelto:$devuelto")

            // ✅ Crear el body con los filtros
            val request = UserLoansRequestDTO(
                vigent = vigente,
                collected = recogido,
                returned = devuelto
            )

            val response = userApiService.getAllLoans(request)

            if (response.isSuccessful && response.body()?.success == true) {
                val loans = response.body()?.data?.map { item ->
                    UserLoan(
                        loanId = item.loanId,
                        title = item.title,
                        description = item.description ?: "",
                        service = item.service,
                        requestDate = item.requestDate,
                        limitDate = item.limitDate,
                        startDate = item.startDate,
                        endDate = item.endDate
                    )
                } ?: emptyList()

                println("✅ ${loans.size} préstamos obtenidos")
                Result.success(loans)
            } else {
                val errorMessage = response.body()?.message ?: "Error al obtener préstamos"
                println("❌ Error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            println("❌ Excepción: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateLoan(
        loanId: String,
        fechaInicio: String?,
        fechaFin: String?
    ): Result<Unit> {
        return try {
            println("📋 Admin: Actualizando préstamo $loanId")
            println("📋 fechaInicio: $fechaInicio, fechaFin: $fechaFin")

            val request = UpdateLoanRequestDTO(
                fechaInicio = fechaInicio,
                fechaFin = fechaFin
            )
            val response = userApiService.updateLoan(loanId, request)

            if (response.isSuccessful && response.body()?.success == true) {
                println("✅ Préstamo actualizado exitosamente")
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al actualizar préstamo"
                println("❌ Error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            println("❌ Excepción: ${e.message}")
            Result.failure(e)
        }
    }
}

