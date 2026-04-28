package com.example.mercader.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mercader.ui.screens.games.GameFormScreen

fun NavGraphBuilder.addGameFormNav(navController: NavHostController) {
    composable(
        route = "game_form?edit={edit}",
        arguments = listOf(
            navArgument("edit") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) {
        GameFormScreen(onEventSaved = { navController.popBackStack() })
    }
}
