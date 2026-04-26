package com.example.mercader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mercader.ui.screens.games.GameFormScreen
import com.example.mercader.ui.screens.games.GameFormViewModel
import com.example.mercader.ui.screens.games.TestScreen
import com.example.mercader.ui.theme.MercaderTheme
import dagger.hilt.android.AndroidEntryPoint

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
                    val viewModel: GameFormViewModel = viewModel()

                    GameFormScreen(
                        viewModel = viewModel,
                        onEventSaved = {
                        }
                    )

                    //TestScreen()
                }
            }
        }
    }
}