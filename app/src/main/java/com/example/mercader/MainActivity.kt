package com.example.mercader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.mercader.ui.screens.games.GameFormScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.ui.screens.games.CollectionScreen
import com.example.mercader.ui.screens.games.CollectionViewModel
import com.example.mercader.ui.screens.games.GameFormViewModel
import com.example.mercader.ui.theme.MercaderTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.mercader.common.components.InProgressModal
import com.example.mercader.domain.models.Game
import com.example.mercader.ui.screens.cart.CartScreen
import com.example.mercader.ui.screens.cart.CartViewModel
import com.example.mercader.ui.screens.games.FilterViewModel
import com.example.mercader.ui.screens.home.AdminHome
import com.example.mercader.ui.screens.home.UserHome
import com.example.mercader.common.utils.CartManager
import javax.inject.Inject

sealed class AppScreen {
    object AdminHome    : AppScreen()
    object UserHome     : AppScreen()
    object GameForm     : AppScreen()
    object Stock        : AppScreen()
    object Cart         : AppScreen()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var cartManager: CartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MercaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ── Estado de navegación ──────────────────────────────
                    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.AdminHome) }
                    var gameToEdit: Game? by remember { mutableStateOf(null) }

                    // ── Router principal ──────────────────────────────────
                    when (currentScreen) {

                        is AppScreen.AdminHome -> {
                            AdminHome(
                                onNavigateToGameForm = { currentScreen = AppScreen.GameForm },
                                onNavigateToStock    = { currentScreen = AppScreen.Stock },
                                onSwitchToUser       = { currentScreen = AppScreen.UserHome }
                            )
                        }

                        is AppScreen.UserHome -> {
                            val viewModel: CollectionViewModel = hiltViewModel()
                            val filterViewModel: FilterViewModel = hiltViewModel()
                            UserHome(
                                onSwitchToAdmin = { currentScreen = AppScreen.AdminHome },
                                onNavigateToCart = { currentScreen = AppScreen.Cart },
                                cartManager = cartManager,  // ← PASAR cartManager
                                collectionViewModel = viewModel,
                                filterViewModel = filterViewModel
                            )
                        }

                        is AppScreen.GameForm -> {
                            val viewModel: GameFormViewModel = hiltViewModel()

                            GameFormScreen(
                                viewModel = viewModel,
                                gameToEdit = gameToEdit,
                                onEventSaved = {
                                    gameToEdit = null
                                    currentScreen = AppScreen.AdminHome
                                },
                                onClose = {
                                    gameToEdit = null
                                    currentScreen = AppScreen.AdminHome
                                }
                            )
                        }

                        is AppScreen.Stock -> {
                            val viewModel: CollectionViewModel = hiltViewModel()
                            CollectionScreen(
                                viewModel = viewModel,
                                cartManager = cartManager,  // ← PASAR cartManager a Stock
                                onBack = { currentScreen = AppScreen.AdminHome },
                                onEditGame = { game ->
                                    gameToEdit = game
                                    currentScreen = AppScreen.GameForm
                                }
                            )
                        }

                        is AppScreen.Cart -> {
                            val cartViewModel: CartViewModel = hiltViewModel()
                            CartScreen(
                                onBack = { currentScreen = AppScreen.UserHome },
                                viewModel = cartViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}