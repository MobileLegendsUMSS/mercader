package com.example.mercader.ui.screens.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Difficulty
import com.example.mercader.data.remote.models.Editorial
import com.example.mercader.domain.models.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionViewModel : ViewModel() {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadGames()
    }

    fun loadGames() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Cuando el back este up ponganse las pilas
                _games.value = getMockGames()
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }
    private fun getMockGames(): List<Game> = listOf(
        Game(
            id = "1",
            title = "Catan",
            description = "Juegazo dadasd",
            tutorial = "http.hola.com",
            category = Category("5","Estrategia"),
            nMinPerson = 3,
            nMaxPerson=5,
            minMinutes=30,
            maxMinutes=60,
            difficulty = Difficulty("5","Estrategia"),
            editorial = Editorial("5","Estrategia"),
            stock =20,
            price=21f
        ),
        Game(
            id = "1",
            title = "kuy",
            description = "ykuyy dadasd",
            tutorial = "yiuyui.hola",
            category = Category("5","Estrategia"),
            nMinPerson = 3,
            nMaxPerson=5,
            minMinutes=30,
            maxMinutes=60,
            difficulty = Difficulty("5","Estrategia"),
            editorial = Editorial("5","Estrategia"),
            stock =20,
            price=21f
        ),
        Game(
            id = "1",
            title = "Hola",
            description = "sdas dadasd",
            tutorial = "asdasda.hola",
            category = Category("5","Estrategia"),
            nMinPerson = 3,
            nMaxPerson=5,
            minMinutes=30,
            maxMinutes=60,
            difficulty = Difficulty("5","Estrategia"),
            editorial = Editorial("5","Estrategia"),
            stock =20,
            price=21f
        )
    )
}
