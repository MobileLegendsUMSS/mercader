package com.example.mercader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.ui.screens.games.CollectionScreen
import com.example.mercader.ui.screens.games.CollectionViewModel
import com.example.mercader.domain.usecases.CreateReservationUseCase
import com.example.mercader.domain.usecases.CreateRentalUseCase
import com.example.mercader.ui.theme.MercaderTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var createReservationUseCase: CreateReservationUseCase
    @Inject lateinit var createRentalUseCase: CreateRentalUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MercaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: CollectionViewModel = hiltViewModel()
                    CollectionScreen(
                        viewModel = viewModel,
                        createReservationUseCase = createReservationUseCase,
                        createRentalUseCase = createRentalUseCase
                    )
                }
            }
        }
    }
}