package com.example.mercader.data.remote.models

data class GameRequestDTO(
    val titulo: String,
    val descripcion: String,
    val tutorial: String,
    val cant_min_pers: Int,
    val cant_max_pers: Int,
    val duracion_min: Int,
    val duracion_max: Int,
    val id_dificultad: String,
    val id_editorial: String,
    val cantidad: Int,
    val precio: Float
)