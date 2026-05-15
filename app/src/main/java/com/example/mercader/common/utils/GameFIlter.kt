// domain/utils/GameFilter.kt
package com.example.mercader.common.utils

import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Difficulty
import com.example.mercader.data.remote.models.Editorial
import com.example.mercader.domain.models.Game

data class GameFilters(
    val category: Category? = null,
    val minPlayers: Int = 1,
    val maxPlayers: Int = 8,
    val minDuration: Int = 0,
    val maxDuration: Int = 240,
    val difficulty: Difficulty? = null,
    val editorial: Editorial? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null
)

object GameFilter {

    /**
     * Filtra una lista de juegos según los criterios especificados
     * @param games Lista original de juegos
     * @param filters Filtros a aplicar
     * @return Lista de juegos filtrada
     */
    fun applyFilters(games: List<Game>, filters: GameFilters?): List<Game> {
        if (filters == null) return games

        return games.filter { game ->
            var matches = true

            // Filtrar por categoría (usando id para comparar)
            if (filters.category != null) {
                matches = game.category.id == filters.category.id
            }

            // Filtrar por rango de jugadores
            if (matches) {
                matches = game.nMinPerson >= filters.minPlayers &&
                        game.nMaxPerson <= filters.maxPlayers
            }

            // Filtrar por rango de duración
            if (matches) {
                matches = game.minMinutes >= filters.minDuration &&
                        game.maxMinutes <= filters.maxDuration
            }

            // Filtrar por dificultad (usando id para comparar)
            if (matches && filters.difficulty != null) {
                matches = game.difficulty.id == filters.difficulty.id
            }

            // Filtrar por editorial (usando id para comparar)
            if (matches && filters.editorial != null) {
                matches = game.editorial.id == filters.editorial.id
            }

            // Filtrar por rango de precio
            if (matches && filters.minPrice != null) {
                matches = game.price >= filters.minPrice
            }

            if (matches && filters.maxPrice != null) {
                matches = game.price <= filters.maxPrice
            }

            matches
        }
    }

    /**
     * Verifica si hay filtros activos
     */
    fun hasActiveFilters(filters: GameFilters?): Boolean {
        if (filters == null) return false

        return filters.category != null ||
                filters.minPlayers != 1 ||
                filters.maxPlayers != 8 ||
                filters.minDuration != 0 ||
                filters.maxDuration != 240 ||
                filters.difficulty != null ||
                filters.editorial != null ||
                filters.minPrice != null ||
                filters.maxPrice != null
    }

    /**
     * Obtiene un resumen de los filtros activos para mostrar al usuario
     */
    fun getFiltersSummary(filters: GameFilters?): String {
        if (filters == null || !hasActiveFilters(filters)) {
            return "Sin filtros aplicados"
        }

        val parts = mutableListOf<String>()

        filters.category?.let { parts.add("📁 ${it.descripcion}") }

        if (filters.minPlayers != 1 || filters.maxPlayers != 8) {
            parts.add("👥 ${filters.minPlayers}-${filters.maxPlayers} jug")
        }

        if (filters.minDuration != 0 || filters.maxDuration != 240) {
            parts.add("⏱️ ${filters.minDuration}-${filters.maxDuration} min")
        }

        filters.difficulty?.let { parts.add("⭐ ${it.descripcion}") }
        filters.editorial?.let { parts.add("🏢 ${it.nombre}") }

        if (filters.minPrice != null) parts.add("💰 ≥$${filters.minPrice}")
        if (filters.maxPrice != null) parts.add("💰 ≤$${filters.maxPrice}")

        return parts.joinToString(" • ")
    }
}