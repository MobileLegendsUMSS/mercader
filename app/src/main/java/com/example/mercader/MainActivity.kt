package com.example.mercader

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.hilt.navigation.compose.hiltViewModel

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import com.example.mercader.ui.screens.games.CollectionScreen
import com.example.mercader.ui.screens.games.CollectionViewModel
import com.example.mercader.ui.theme.MercaderTheme

import com.example.mercader.common.components.SplashAuthenticationScreen

import com.example.mercader.ui.screens.auth.AuthViewModel
import com.example.mercader.ui.screens.auth.LoginScreen
import com.example.mercader.ui.screens.auth.SignupScreen
import com.example.mercader.ui.screens.home.AdminHome
import com.example.mercader.ui.screens.home.UserHome
import com.example.mercader.ui.screens.games.GameFormScreen
import com.example.mercader.ui.screens.games.GameFormViewModel
import com.example.mercader.ui.screens.games.FilterViewModel
import com.example.mercader.ui.screens.cart.CartScreen
import com.example.mercader.ui.screens.cart.CartViewModel
import com.example.mercader.ui.screens.profile.ProfileViewModel
import com.example.mercader.ui.screens.profile.ProfileScreen
import com.example.mercader.ui.screens.profile.AdminProfileSimpleScreen

import com.example.mercader.domain.models.Game
import com.example.mercader.common.utils.CartManager
import com.example.mercader.common.utils.ReserveManager

sealed class AppScreen {
    object Splash       : AppScreen()
    object Login        : AppScreen()
    object SignUp       : AppScreen()
    object AdminHome    : AppScreen()
    object UserHome     : AppScreen()
    object GameForm     : AppScreen()
    object Stock        : AppScreen()
    object Cart         : AppScreen()
    object Profile      : AppScreen()
    object AdminProfile : AppScreen()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var cartManager: CartManager
    @Inject
    lateinit var reserveManager: ReserveManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MercaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 🟢 Inicialización de Estados y ViewModels Centralizados
                    var gameToEdit: Game? by remember { mutableStateOf(null) }
                    val collectionViewModel: CollectionViewModel = hiltViewModel()
                    val authViewModel: AuthViewModel = hiltViewModel() // 🟢 Instanciado correctamente con Hilt
                    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }

                    // 🗺️ Router principal
                    when (currentScreen) {
                        is AppScreen.Splash -> {
                            // 🟢 Validar de entrada usando el ViewModel si hay sesión activa en el backend
                            LaunchedEffect(Unit) {
                                authViewModel.checkAuthentication(
                                    onAuthenticated = { isAdmin, rol ->
                                        currentScreen = if (isAdmin) AppScreen.AdminHome else AppScreen.UserHome
                                    },
                                    onNotAuthenticated = {
                                        currentScreen = AppScreen.Login
                                    }
                                )
                            }

                            SplashAuthenticationScreen(
                                onNavigateToHome = { isAdmin, rol ->
                                    currentScreen = if (isAdmin) AppScreen.AdminHome else AppScreen.UserHome
                                },
                                onNavigateToLogin = {
                                    currentScreen = AppScreen.Login
                                }
                            )
                        }

                        is AppScreen.Login -> {
                            LoginScreen(
                                viewModel = authViewModel, // 🟢 Pasamos el ViewModel de Hilt
                                onLoginSuccess = { isAdmin ->
                                    currentScreen = if (isAdmin) AppScreen.AdminHome else AppScreen.UserHome
                                },
                                onNavigateToSignup = { currentScreen = AppScreen.SignUp }
                            )
                        }

                        is AppScreen.SignUp -> {
                            SignupScreen(
                                viewModel = authViewModel, // 🟢 Pasamos el ViewModel de Hilt
                                onSignupSuccess = { isAdmin ->
                                    currentScreen = if (isAdmin) AppScreen.AdminHome else AppScreen.UserHome
                                },
                                onNavigateToLogin = { currentScreen = AppScreen.Login }
                            )
                        }

                        is AppScreen.AdminHome -> {
                            AdminHome(
                                onNavigateToGameForm = { currentScreen = AppScreen.GameForm },
                                onNavigateToStock = { currentScreen = AppScreen.Stock },
                                onSwitchToUser = { currentScreen = AppScreen.UserHome },
                                onNavigateToProfile = { currentScreen = AppScreen.AdminProfile }
                            )
                        }

                        is AppScreen.UserHome -> {
                            val filterViewModel: FilterViewModel = hiltViewModel()
                            UserHome(
                                onSwitchToAdmin = { currentScreen = AppScreen.AdminHome },
                                onNavigateToCart = { currentScreen = AppScreen.Cart },
                                onNavigateToProfile = { currentScreen = AppScreen.Profile },
                                collectionViewModel = collectionViewModel,
                                cartManager = cartManager,
                                reserveManager = reserveManager,
                                filterViewModel = filterViewModel
                            )
                        }

                        is AppScreen.GameForm -> {
                            val viewModel: GameFormViewModel = hiltViewModel()

                            if (gameToEdit == null) {
                                viewModel.resetForm()
                            }
                             
                            GameFormScreen(
                                viewModel = viewModel,
                                gameToEdit = gameToEdit,
                                onEventSaved = {
                                    gameToEdit = null
                                    currentScreen = AppScreen.AdminHome
                                    collectionViewModel.refreshGames()
                                },
                                onClose = {
                                    gameToEdit = null
                                    currentScreen = AppScreen.AdminHome
                                    viewModel.clearGameToEdit()
                                }
                            )
                        }

                        is AppScreen.Stock -> {
                            CollectionScreen(
                                viewModel = collectionViewModel,
                                cartManager = cartManager,
                                onBack = { currentScreen = AppScreen.AdminHome },
                                reserveManager = reserveManager,
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

                        is AppScreen.Profile -> {
                            val profileViewModel: ProfileViewModel = hiltViewModel()
                            ProfileScreen(
                                onBack = { currentScreen = AppScreen.UserHome },
                                onLogout = {
                                    authViewModel.resetState()
                                    currentScreen = AppScreen.Login
                                },
                                viewModel = profileViewModel,
                                cartManager = cartManager,
                                reserveManager = reserveManager
                            )
                        }
                        is AppScreen.AdminProfile -> {
                            AdminProfileSimpleScreen(
                                onBack = { currentScreen = AppScreen.AdminHome },
                                onLogout = {
                                    authViewModel.resetState()
                                    currentScreen = AppScreen.Login
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}