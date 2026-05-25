package com.example.mercader.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.domain.usecases.AuthenticationUseCase
import kotlinx.coroutines.Dispatchers

import androidx.compose.runtime.State
import com.example.mercader.ui.screens.auth.AuthViewModel

@Composable
fun rememberAuthToken(): State<String?> {
    val viewModel: AuthViewModel = hiltViewModel()

    return produceState<String?>(initialValue = null) {
        try {
            value = viewModel.getAuthToken()
        } catch (e: Exception) {
            e.printStackTrace()
            value = null
        }
    }
}

@Composable
fun rememberIsUserAuthenticated(): State<Boolean> {
    val viewModel: AuthViewModel = hiltViewModel()

    return produceState(initialValue = false) {
        try {
            value = viewModel.isUserAuthenticated()
        } catch (e: Exception) {
            e.printStackTrace()
            value = false
        }
    }

}

