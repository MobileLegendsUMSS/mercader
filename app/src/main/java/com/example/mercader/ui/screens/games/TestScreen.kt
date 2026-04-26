package com.example.mercader.ui.screens.games

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.common.components.DeleteGameModal
import com.example.mercader.domain.usecases.DeleteGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class TestViewModel @Inject constructor(
    private val deleteGameUseCase: DeleteGameUseCase
): ViewModel() {
    fun deleteGame(id: String, justificacionRetiro: String) {
        viewModelScope.launch {
            Log.d("TestViewModel", "========== INICIO DELETE ==========")
            Log.d("TestViewModel", "ID del juego: $id")
            Log.d("TestViewModel", "Justificación: $justificacionRetiro")

            val result = deleteGameUseCase.deleteGame(id, justificacionRetiro)

            if (result.isSuccess) {
                Log.d("TestViewModel", "✅ ÉXITO: Juego eliminado")
            } else {
                Log.e("TestViewModel", "❌ ERROR: ${result.exceptionOrNull()?.message}")
                result.exceptionOrNull()?.printStackTrace()
            }
            Log.d("TestViewModel", "========== FIN DELETE ==========")
        }
    }
}

@Composable
fun TestScreen(
    viewModel: TestViewModel = hiltViewModel(),
) {
    var showModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { showModal = true }) {
            Text("Retirar Juego")
        }
    }

    if (showModal) {
        DeleteGameModal(
            gameName = "Catan",
            onConfirm = { justificacionRetiro ->
                Log.d("TestScreen", "Justificacion: $justificacionRetiro")
                viewModel.deleteGame(
                    id = "69e5a7951edc217a4d7ab772",
                    justificacionRetiro = justificacionRetiro
                )
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
}