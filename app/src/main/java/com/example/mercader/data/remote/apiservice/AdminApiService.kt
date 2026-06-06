package com.example.mercader.data.remote.apiservice

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

data class ChangeRoleRequest(
    val id_usuario: String,
    val nuevo_rol: String
)

data class UserWithRoleResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<UserWithRole>
)
data class BaseResponse(
    val success: Boolean,
    val message: String? = null
)
data class UserWithRole(
    val _id: String,
    val nombre: String,
    val nombres: String?,
    val apellidos: String?,
    val telefono: String,
    val correo_contacto: String,
    val mercapoints: Int,
    val rol: String
)

data class ChangeRoleResponse(
    val success: Boolean,
    val message: String? = null,
    val requiereLogout: Boolean,
    val data: UserRoleChangeData?
)
data class UserRoleChangeData(
    val id: String,
    val nombre: String,
    val rolAnterior: String,
    val rolNuevo: String
)

interface AdminApiService {
    @GET("admin/usuarios")
    suspend fun getUsuariosConRoles(): Response<UserWithRoleResponse>

    @PUT("admin/usuarios/rol")
    suspend fun cambiarRol(
        @Body request: ChangeRoleRequest
    ): Response<ChangeRoleResponse>
}