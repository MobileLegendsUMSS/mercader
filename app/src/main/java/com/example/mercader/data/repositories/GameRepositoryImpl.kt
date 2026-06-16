package com.example.mercader.data.repositories

import com.example.mercader.common.utils.NetworkHandler
import com.example.mercader.data.remote.apiservice.GameApiService
import com.example.mercader.data.remote.models.GameRequestDTO
import com.example.mercader.data.remote.dto.DeleteGameRequestDto
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.repositories.GameRepository
import com.example.mercader.data.remote.models.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import javax.net.ssl.SSLSocketFactory
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import android.net.Uri
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

class GameRepositoryImpl @Inject constructor(
    private val apiService: GameApiService,
    private val networkHandler: NetworkHandler,
    private val tokenRepository: com.example.mercader.data.local.ITokenRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : GameRepository {

    override suspend fun saveGame(game: Game): Result<Unit> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexion a internet"))
            }

            val fields = mutableMapOf<String, okhttp3.RequestBody>()
            fun addPart(key: String, value: Any) {
                fields[key] = value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            }

            addPart("titulo", game.title)
            addPart("descripcion", game.description)
            addPart("tutorial", game.tutorial)
            addPart("cant_min_pers", game.nMinPerson)
            addPart("cant_max_pers", game.nMaxPerson)
            addPart("duracion_min", game.minMinutes)
            addPart("duracion_max", game.maxMinutes)
            addPart("precio", game.price)
            addPart("disponible", true)
            addPart("activo", true)
            addPart("cantidad", game.stock)
            addPart("id_dificultad", game.difficulty.id)
            addPart("id_editorial", game.editorial.id)

            val servicesList = listOfNotNull(
                if (game.isPurchaseAvailable) "compra" else null,
                if (game.isRentAvailable) "alquiler" else null,
                if (game.isLoanAvailable) "prestamo" else null
            )
            val serviceParts = servicesList.map { service ->
                MultipartBody.Part.createFormData("services", service)
            }

            var imagePart: MultipartBody.Part? = null
            game.imageUrl?.let { uriString ->
                try {
                    val uri = Uri.parse(uriString)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val tempFile = java.io.File.createTempFile("upload", ".jpg", context.cacheDir)
                        tempFile.outputStream().use { output ->
                            inputStream.copyTo(output)
                        }
                        val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("portada", tempFile.name, requestFile)
                    }
                } catch (e: Exception) {
                    Log.e("GameRepository", "Error al procesar la imagen: ${e.message}")
                }
            }

            if (imagePart == null) {
                val emptyBody = "".toRequestBody("image/jpeg".toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData("portada", "empty.jpg", emptyBody)
            }

            val response = apiService.saveGame(
                idCategory = game.category.id,
                portada = imagePart!!,
                fields = fields,
                services = serviceParts
            )
            Log.d("GameRepository", "Respuesta de API: $response")
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)

        }
    }

    override suspend fun getGameTypes(): Result<List<Category>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getGameTypes()
            if (response.isSuccessful) {
                val gameCategoryResponse = response.body()
                if (gameCategoryResponse != null && gameCategoryResponse.success) {
                    val gameTypes = gameCategoryResponse.data.map { Category(it._id, it.descripcion)  }
                    Result.success(gameTypes)
                } else {
                    Result.failure(Exception("Error en la respuesta del servidor"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGameServices(gameId: String): Result<List<String>> {
        return try {
            val response = apiService.getGameServices(gameId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val services = body.services ?: emptyList()
                Result.success(services)
            } else {
                Result.failure(Exception("Error al obtener servicios del juego"))
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error en getGameServices: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getDifficulties(): Result<List<Difficulty>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getDifficulties()
            if (response.isSuccessful) {
                val gameDifficultiesResponse = response.body()
                if (gameDifficultiesResponse != null && gameDifficultiesResponse.success) {
                    val gameDiff = gameDifficultiesResponse.data.map { Difficulty(it._id, it.descripcion)  }
                    Result.success(gameDiff)
                } else {
                    Result.failure(Exception("Error en la respuesta del servidor"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEditorials(): Result<List<Editorial>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getEditorials()
            if (response.isSuccessful) {
                val gameEditorialResponse = response.body()
                if (gameEditorialResponse != null && gameEditorialResponse.success) {
                    val gameEditorials  = gameEditorialResponse.data.map {Editorial(it._id,it.nombre)  }
                    Result.success(gameEditorials)
                } else {
                    Result.failure(Exception("Error en la respuesta del servidor"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapResponseToGames(data: List<GameResponseDTO>?): List<Game> {
        if (data == null) return emptyList()
        return data.map {
            val cats = it.categorias ?: emptyList()
            val svcs = it.servicios ?: emptyList()
            Game(
                it._id ?: "",
                it.titulo ?: "Sin título",
                it.descripcion ?: "Sin descripción",
                it.tutorial ?: "",
                if (cats.isNotEmpty()) Category(cats[0], cats[0]) else Category("", ""),
                it.cant_min_pers ?: 1,
                it.cant_max_pers ?: 4,
                it.duracion_min ?: 30,
                it.duracion_max ?: 60,
                if (it.id_dificultad != null) Difficulty(it.id_dificultad._id, it.id_dificultad.descripcion) else Difficulty("", ""),
                if (it.id_editorial != null) Editorial(it.id_editorial._id, it.id_editorial.nombre) else Editorial("", ""),
                it.activo==true,
                it.cantidad ?: 0,
                it.precio ?: 0.0f,
                svcs.contains("compra"),
                svcs.contains("alquiler"),
                svcs.contains("prestamo"),
                imageUrl = it.portada
            )
        }
    }

    override suspend fun getGames(): Result<List<Game>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión"))
            }

            val response = apiService.getGames()
            Log.d("ResponseCollection"," ${response}")
            if (response.isSuccessful) {
                val gamesResponse = response.body()
                if (gamesResponse != null && gamesResponse.success) {
                    Result.success(mapResponseToGames(gamesResponse.data))
                } else {
                    Result.failure(Exception("Error en la respuesta del servidor"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecentGames(): Result<List<Game>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) return Result.failure(IOException("No hay conexión"))
            val response = apiService.getRecentGames()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(mapResponseToGames(response.body()!!.data))
            } else Result.failure(HttpException(response))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getMostVisitedGames(): Result<List<Game>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) return Result.failure(IOException("No hay conexión"))
            val response = apiService.getGames()
            if (response.isSuccessful && response.body()?.success == true) {
                val sortedList = response.body()?.data?.sortedByDescending { it.visitas ?: 0 }?.take(10)
                Result.success(mapResponseToGames(sortedList))
            } else {
                Result.failure(Exception("Error en la respuesta"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMostSoldGames(): Result<List<Game>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) return Result.failure(IOException("No hay conexión"))
            val response = apiService.getGames()
            if (response.isSuccessful && response.body()?.success == true) {
                val sortedList = response.body()?.data?.sortedByDescending { it.ventas ?: 0 }?.take(10)
                Result.success(mapResponseToGames(sortedList))
            } else {
                Result.failure(Exception("Error en la respuesta"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMostBorrowedGames(): Result<List<Game>> {
        return try {
            if (!networkHandler.isNetworkAvailable()) return Result.failure(IOException("No hay conexión"))
            val response = apiService.getGames()
            if (response.isSuccessful && response.body()?.success == true) {
                val sortedList = response.body()?.data?.sortedByDescending { it.prestamos ?: 0 }?.take(10)
                Result.success(mapResponseToGames(sortedList))
            } else {
                Result.failure(Exception("Error en la respuesta"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGamePartial(gameId: String, updatedFields: Map<String, Any>): Result<Unit> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión a internet"))
            }

            if (updatedFields.isEmpty()) {
                Log.d("GameRepository", "No hay campos para actualizar")
                return Result.success(Unit)
            }

            Log.d("GameRepository", "Actualizando juego $gameId con ${updatedFields.size} campos")

            val fieldUpdates = convertToFieldUpdates(updatedFields)

            val errors = mutableListOf<String>()
            var successCount = 0

            for ((index, fieldUpdate) in fieldUpdates.withIndex()) {
                try {
                    Log.d("GameRepository", "Actualizando campo ${index + 1}/${fieldUpdates.size}: ${fieldUpdate.fieldName}")

                    val response = apiService.editGame(gameId, fieldUpdate)

                    if (response.isSuccessful) {
                        successCount++
                        Log.d("GameRepository", "Campo ${fieldUpdate.fieldName} actualizado")
                    } else {
                        val errorMsg = "Error al actualizar ${fieldUpdate.fieldName}: ${response.code()}"
                        Log.e("GameRepository", "$errorMsg")
                        errors.add(errorMsg)
                    }

                } catch (e: Exception) {
                    val errorMsg = "Error al actualizar ${fieldUpdate.fieldName}: ${e.message}"
                    Log.e("GameRepository", "✗ $errorMsg")
                    errors.add(errorMsg)
                }
            }

            return if (errors.isEmpty()) {
                Log.d("GameRepository", "✅ Todos los $successCount campos actualizados correctamente")
                Result.success(Unit)
            } else {
                val errorSummary = "Se actualizaron $successCount de ${fieldUpdates.size} campos. Errores: ${errors.joinToString("; ")}"
                Log.e("GameRepository", "❌ $errorSummary")
                Result.failure(Exception(errorSummary))
            }

        } catch (e: HttpException) {
            Log.e("GameRepository", "HTTP Error: ${e.code()}")
            Result.failure(Exception("Error del servidor: ${e.code()}"))
        } catch (e: IOException) {
            Log.e("GameRepository", "Network Error: ${e.message}")
            Result.failure(Exception("Error de conexión: Verifica tu internet"))
        } catch (e: Exception) {
            Log.e("GameRepository", "Error inesperado: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun convertToFieldUpdates(updatedFields: Map<String, Any>): List<GameEditDTO> {
        val fieldUpdates = mutableListOf<GameEditDTO>()
        Log.e("SERECONOCE","$updatedFields")
        updatedFields.forEach { (key, value) ->
            when (key) {
                "title" -> {
                    fieldUpdates.add(GameEditDTO("titulo", value))
                }
                "description" -> {
                    fieldUpdates.add(GameEditDTO("descripcion", value))
                }
                "tutorial" -> {
                    fieldUpdates.add(GameEditDTO("tutorial", value))
                }
                "nMinPerson" -> {
                    fieldUpdates.add(GameEditDTO("cant_min_pers", value))
                }
                "nMaxPerson" -> {
                    fieldUpdates.add(GameEditDTO("cant_max_pers", value))
                }
                "minMinutes" -> {
                    fieldUpdates.add(GameEditDTO("duracion_min", value))
                }
                "maxMinutes" -> {
                    fieldUpdates.add(GameEditDTO("duracion_max", value))
                }
                "category" -> {
                    val categoryMap = value as? Map<*, *>
                    categoryMap?.get("descripcion")?.let { descripcion ->
                        fieldUpdates.add(GameEditDTO("categoria", descripcion))
                    }
                }
                "difficulty" -> {
                    val difficultyMap = value as? Map<*, *>
                    difficultyMap?.get("descripcion")?.let { descripcion ->
                        fieldUpdates.add(GameEditDTO("dificultad", descripcion))
                    }
                }
                "editorial" -> {
                    val editorialMap = value as? Map<*, *>
                    editorialMap?.get("nombre")?.let { nombre ->
                        fieldUpdates.add(GameEditDTO("editorial", nombre))
                    }
                }
                "stock" -> {
                    fieldUpdates.add(GameEditDTO("cantidad", value))
                }
                "price" -> {
                    fieldUpdates.add(GameEditDTO("precio", value))
                }
            }
        }

        return fieldUpdates
    }

    override suspend fun deleteGame(id: String, justificacionRetiro: String): Result<Unit> {
        Log.d("GameRepository", "========== REPOSITORY DELETE ==========")
        Log.d("GameRepository", "ID: $id")
        Log.d("GameRepository", "Justificación: $justificacionRetiro")

        return try {
            val request = DeleteGameRequestDto(justificacionRetiro = justificacionRetiro)
            Log.d("GameRepository", "Request creado: $request")

            Log.d("GameRepository", "Llamando a apiService.deleteGame...")
            val response = apiService.deleteGame(id, request)

            Log.d("GameRepository", "Respuesta de API: $response")
            Result.success(Unit)

        } catch (e: HttpException) {
            Log.e("GameRepository", "❌ Error HTTP: ${e.code()} - ${e.message()}")
            Log.e("GameRepository", "Response body: ${e.response()?.errorBody()?.string()}")
            Result.failure(Exception("Error del servidor: ${e.code()}"))
        } catch (e: IOException) {
            Log.e("GameRepository", "❌ Error de red: ${e.message}")
            Result.failure(Exception("Error de conexión: Verifica tu internet"))
        } catch (e: Exception) {
            Log.e("GameRepository", "❌ Error inesperado: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }


    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver

            // Verificar que el URI existe
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("GameRepository", "No se pudo abrir el stream para URI: $uri")
                return null
            }

            // Obtener el nombre del archivo
            val fileName = if (uri.scheme == "content") {
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) it.getString(nameIndex) else "image_${System.currentTimeMillis()}.jpg"
                    } else {
                        "image_${System.currentTimeMillis()}.jpg"
                    }
                } ?: "image_${System.currentTimeMillis()}.jpg"
            } else {
                uri.path?.substringAfterLast("/") ?: "image_${System.currentTimeMillis()}.jpg"
            }

            // Crear archivo temporal
            val tempFile = File(context.cacheDir, fileName)

            // Copiar el archivo
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Verificar que el archivo se creó correctamente
            if (tempFile.exists() && tempFile.length() > 0) {
                Log.d("GameRepository", "✅ Archivo creado: ${tempFile.absolutePath}, tamaño: ${tempFile.length()} bytes")
                tempFile
            } else {
                Log.e("GameRepository", "❌ Archivo vacío o no creado")
                null
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "❌ Error al obtener archivo desde URI: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // En GameRepositoryImpl.kt - updateGameImage
    override suspend fun updateGameImage(
        gameId: String,
        imageUri: Uri,
        fieldName: String ,
        fieldValue: String
    ): Result<Unit> {
        return try {
            if (!networkHandler.isNetworkAvailable()) {
                return Result.failure(IOException("No hay conexión a internet"))
            }

            Log.d("GameRepository", "Subiendo imagen para juego $gameId")
            Log.d("GameRepository", "URI: $imageUri")

            val file = getFileFromUri(imageUri)
            if (file == null) {
                return Result.failure(Exception("No se pudo obtener el archivo de imagen"))
            }

            val mimeType = context.contentResolver.getType(imageUri)
            if (mimeType != null && !mimeType.startsWith("image/")) {
                return Result.failure(Exception("El archivo seleccionado no es una imagen válida."))
            }

            val mediaType = mimeType?.toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull()
            val requestBody = file.asRequestBody(mediaType)
            val portadaPart = MultipartBody.Part.createFormData(
                "portada",
                file.name,
                requestBody
            )

            Log.d("GameRepository", "Enviando imagen: ${file.name}, tamaño: ${file.length()} bytes")
            Log.d("GameRepository", "fieldName: $fieldName, fieldValue: $fieldValue")


            val filePart = MultipartBody.Part.createFormData(
                "portada",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
            val correcto1 = fieldName.toRequestBody("text/plain".toMediaTypeOrNull())
            val correcto2 = fieldValue.toRequestBody("text/plain".toMediaTypeOrNull())
            // IMPORTANTE: Agregar timeout y manejar cancelación
            val response = withTimeoutOrNull(60000L.milliseconds) {
                apiService.updateGameWithImage(
                    gameId = gameId,
                    fieldName = correcto1,
                    fieldValue = correcto2,
                    portada = portadaPart
                )
            }

            if (response == null) {
                Log.e("GameRepository", "❌ Timeout - La solicitud tomó más de 60 segundos")
                return Result.failure(Exception("Tiempo de espera agotado. Intenta con una imagen más pequeña."))
            }

            if (response.isSuccessful) {
                Log.d("GameRepository", "✅ Imagen subida correctamente")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Error al subir la imagen (${response.code()})"
                Log.e("GameRepository", "❌ Error al subir imagen: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.util.concurrent.CancellationException) {
            Log.e("GameRepository", "❌ Solicitud cancelada: ${e.message}")
            Result.failure(Exception("La operación fue cancelada. Intenta nuevamente."))
        } catch (e: Exception) {
            Log.e("GameRepository", "❌ Error inesperado al subir imagen: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }


}