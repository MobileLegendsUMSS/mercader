package com.example.mercader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import android.view.WindowManager

import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
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
import com.example.mercader.ui.screens.admin.AdminStockScreen
import com.example.mercader.ui.screens.admin.AdminRoleManagementScreen
import android.widget.Toast
import com.example.mercader.domain.models.Game
import com.example.mercader.common.utils.CartManager
import com.example.mercader.common.utils.ReserveManager
import com.example.mercader.common.utils.RefreshTokenService  // ? Importar
import com.example.mercader.ui.screens.admin.AdminLoanManagementScreen
import com.example.mercader.ui.screens.admin.AdminPurchaseManagementScreen
import com.example.mercader.ui.screens.reports.ReportScreen
import com.example.mercader.ui.screens.reports.ReportViewModel
import kotlinx.coroutines.launch  // ? Importar para coroutine scope

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
    object AdminLoanManagement : AppScreen()
    object Reports      : AppScreen()
    object AdminPurchaseManagement : AppScreen()
    object AdminManageUsers : AppScreen()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var reserveManager: ReserveManager

    @Inject
    lateinit var refreshTokenService: RefreshTokenService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        @Suppress("DEPRECATION")
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)


        setContent {
            MercaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ? Estados y ViewModels
                    var gameToEdit: Game? by remember { mutableStateOf(null) }
                    val collectionViewModel: CollectionViewModel = hiltViewModel()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }

                    // ? CoroutineScope para la Activity
                    val activityScope = rememberCoroutineScope()

                    // ? Iniciar refresh periódico al cargar la pantalla
                    LaunchedEffect(Unit) {
                        refreshTokenService.startPeriodicRefresh(
                            onRoleChanged = {
                                // Forzar logout cuando el rol cambia
                                activityScope.launch {
                                    refreshTokenService.forceLogout()
                                    authViewModel.resetState()
                                    currentScreen = AppScreen.Login
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Tu rol ha cambiado. Por favor inicia sesión nuevamente.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                    }

                    // ?? Router principal
                    when (currentScreen) {
                        is AppScreen.Splash -> {
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

                        is AppScreen.AdminPurchaseManagement -> {
                            AdminPurchaseManagementScreen(
                                onBack = { currentScreen = AppScreen.AdminHome }
                            )
                        }

                        is AppScreen.Login -> {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = { isAdmin ->
                                    currentScreen = if (isAdmin) AppScreen.AdminHome else AppScreen.UserHome
                                },
                                onNavigateToSignup = { currentScreen = AppScreen.SignUp }
                            )
                        }

                        is AppScreen.SignUp -> {
                            SignupScreen(
                                viewModel = authViewModel,
                                onSignupSuccess = { isAdmin ->
                                    currentScreen = if (isAdmin) AppScreen.AdminHome else AppScreen.UserHome
                                },
                                onNavigateToLogin = { currentScreen = AppScreen.Login }
                            )
                        }

                        is AppScreen.AdminHome -> {
                            // ? Obtener rol de manera suspendida pero en composición
                            var rol by remember { mutableStateOf("usuario") }

                            LaunchedEffect(Unit) {
                                rol = authViewModel.getUserRol() ?: "usuario"
                            }
                            AdminHome(
                                onNavigateToGameForm = { currentScreen = AppScreen.GameForm },
                                onNavigateToStock = { currentScreen = AppScreen.Stock },
                                onNavigateToProfile = { currentScreen = AppScreen.AdminProfile },
                                onNavigateToReports = { currentScreen = AppScreen.Reports },
                                onNavigateToLoanManagement = { currentScreen = AppScreen.AdminLoanManagement },
                                onNavigateToPurchases = { currentScreen = AppScreen.AdminPurchaseManagement },
                                onNavigateToManageUsers = { currentScreen = AppScreen.AdminManageUsers },
                                isSuperAdmin = rol == "superadmin"
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
                            val adminStockViewModel: com.example.mercader.ui.screens.admin.AdminStockViewModel = hiltViewModel()
                            AdminStockScreen(
                                onBack = { currentScreen = AppScreen.AdminHome },
                                onEditGame = { game ->
                                    gameToEdit = game
                                    currentScreen = AppScreen.GameForm
                                },
                                onDeleteGame = { game ->
                                    adminStockViewModel.deleteGame(
                                        gameId = game.id,
                                        onSuccess = {
                                            collectionViewModel.refreshGames()
                                        },
                                        onError = { error ->
                                            Toast.makeText(
                                                this@MainActivity,
                                                error,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
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
                                    // ? Detener refresh y limpiar
                                    activityScope.launch {
                                        refreshTokenService.stopPeriodicRefresh()
                                        refreshTokenService.forceLogout()
                                    }
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
                                    // ? Detener refresh y limpiar
                                    activityScope.launch {
                                        refreshTokenService.stopPeriodicRefresh()
                                        refreshTokenService.forceLogout()
                                    }
                                    authViewModel.resetState()
                                    currentScreen = AppScreen.Login
                                }
                            )
                        }

                        is AppScreen.AdminLoanManagement -> {
                            AdminLoanManagementScreen(
                                onBack = { currentScreen = AppScreen.AdminHome }
                            )
                        }

                        is AppScreen.Reports -> {
                            val reportViewModel: ReportViewModel = hiltViewModel()
                            ReportScreen(
                                viewModel = reportViewModel,
                                onBack = { currentScreen = AppScreen.AdminHome }
                            )
                        }

                        is AppScreen.AdminManageUsers -> {
                            val adminRoleViewModel: com.example.mercader.ui.screens.admin.viewmodel.AdminRoleViewModel = hiltViewModel()
                            AdminRoleManagementScreen(
                                viewModel = adminRoleViewModel,
                                onBack = { currentScreen = AppScreen.AdminHome },
                                onRoleChanged = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Rol actualizado. El usuario deberá volver a iniciar sesión.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}