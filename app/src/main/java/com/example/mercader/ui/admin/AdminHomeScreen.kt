package com.example.mercader.ui.admin
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.setValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import com.example.mercader.common.components.MainHeader
import androidx.compose.material3.Scaffold

import com.example.mercader.ui.screen.game.GameFormScreen

@Composable
fun AdminHomeScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("inicio") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Panel Administrativo", modifier = Modifier.padding(16.dp))
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Nuevo Juego") },
                    selected = currentScreen == "nuevo_juego",
                    onClick = {
                        currentScreen = "nuevo_juego"
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Stock") },
                    selected = currentScreen == "stock",
                    onClick = {
                        currentScreen = "stock"
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                MainHeader(
                    title = "LudoTeca Admin",
                    isAdmin = true,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    "inicio" -> Text("Bienvenido, selecciona una opción del menú")
                    "nuevo_juego" -> GameFormScreen()
                    "stock" -> Text("Pantalla de Stock (Próximamente)")
                }
            }
        }
    }
}