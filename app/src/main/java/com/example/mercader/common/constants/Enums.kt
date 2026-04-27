
package com.example.mercader.common.constants
//Estos enums son temporales hasta que este el backend
// ==============Enums para los selectores==============================//
enum class GameType(val displayName: String) {
    FEAR("Terror"),
    ADVENTURE("Aventura"),
    ECONOMY("Economia")
}

enum class Difficulties(val displayName: String) {
    EASY("Facil"),
    MEDIUM("Media"),
    HARD("Dificil")
}
enum class Editorial(val displayName: String) {
    DEVIR("Devir"),
    ESPAÑA("España"),
    KITTY("Kitty")
}

// ============ ENUMS PARA SLIDERS ============

enum class SliderType(
    val label: String,
    val range: ClosedFloatingPointRange<Float>,
    val steps: Int,
    val formatValue: (Float) -> String
) {
    PEOPLE_COUNT(
        label = "Numero de personas",
        range = 1f..10f,
        steps = 10,
        formatValue = { value ->
            when (value) {
                1f -> "1 persona"
                10f -> "+10 persona"
                else -> "$value personas"
            }
        }
    ),
    DURATION_HOURS(
        label = "Duracion (minutos)",
        range = 30f..300f,
        steps = 5,
        formatValue = { value ->
            if (value ==30f) "30 minutos" else "$value minutos"
        }
    ),
}