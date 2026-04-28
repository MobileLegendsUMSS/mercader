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
import com.example.mercader.ui.screens.games.TestScreen
import com.example.mercader.ui.theme.MercaderTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercader.common.components.InProgressModal
import com.example.mercader.common.components.SidebarMenu
import com.example.mercader.domain.models.Game
import com.example.mercader.ui.screens.home.AdminHome
import com.example.mercader.ui.screens.home.UserHome

sealed class AppScreen {
    object AdminHome    : AppScreen()
    object UserHome    : AppScreen()
    object GameForm     : AppScreen()
    object Stock        : AppScreen()
}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
                    // isAdmin = true por defecto → arranca en AdminHome
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
                            UserHome(
                                onSwitchToAdmin = { currentScreen = AppScreen.AdminHome }
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
                                onBack = { currentScreen = AppScreen.AdminHome },
                                onEditGame = { game ->
                                    gameToEdit = game
                                    currentScreen = AppScreen.GameForm
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
