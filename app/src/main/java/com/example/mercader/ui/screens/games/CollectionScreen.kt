package com.example.mercader.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mercader.common.components.GameCard
import com.example.mercader.common.components.GameDetailDialog
import com.example.mercader.common.components.ReservationGameModal
import com.example.mercader.common.components.RentalGameModal
import com.example.mercader.common.components.ResultModal
import com.example.mercader.domain.models.Game
import com.example.mercader.domain.usecases.CreateReservationUseCase
import com.example.mercader.domain.usecases.CreateRentalUseCase
import kotlinx.coroutines.launch

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    createReservationUseCase: CreateReservationUseCase? = null,
    createRentalUseCase: CreateRentalUseCase? = null,
    modifier: Modifier = Modifier
) {
    val games by viewModel.games.collectAsState()
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var showReservationModal by remember { mutableStateOf(false) }
    var showRentalModal by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var showResultModal by remember { mutableStateOf(false) }
    var resultIsSuccess by remember { mutableStateOf(false) }
    var resultTitle by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(games) { game ->
            GameCard(
                game = game,
                onClick = { selectedGame = game }
            )
        }
    }

    selectedGame?.let { game ->
        GameDetailDialog(
            game = game,
            onDismiss = { selectedGame = null },
            onReserve = {
                showReservationModal = true
            },
            onRent = {
                showRentalModal = true
            }
        )

        if (showReservationModal) {
            ReservationGameModal(
                onDismiss = { showReservationModal = false },
                onConfirm = { date, time ->
                    coroutineScope.launch {
                        isLoading = true
                        val result = createReservationUseCase?.execute(game.id, date, time)
                        isLoading = false
                        showReservationModal = false

                        if (result != null && result.isSuccess) {
                            resultIsSuccess = true
                            resultTitle = "Reservation Confirmed"
                            resultMessage = result.getOrDefault("Reservation created successfully.")
                        } else {
                            resultIsSuccess = false
                            resultTitle = "Reservation Error"
                            resultMessage = result?.exceptionOrNull()?.message ?: "Could not process the request."
                        }
                        showResultModal = true
                    }
                },
                isLoading = isLoading
            )
        }

        if (showRentalModal) {
            RentalGameModal(
                onDismiss = { showRentalModal = false },
                onConfirm = { startDate, returnDate ->
                    coroutineScope.launch {
                        isLoading = true
                        val result = createRentalUseCase?.execute(game.id, startDate, returnDate)
                        isLoading = false
                        showRentalModal = false

                        if (result != null && result.isSuccess) {
                            resultIsSuccess = true
                            resultTitle = "Rental Confirmed"
                            resultMessage = result.getOrDefault("Rental created successfully.")
                        } else {
                            resultIsSuccess = false
                            resultTitle = "Rental Error"
                            resultMessage = result?.exceptionOrNull()?.message ?: "Could not process the request."
                        }
                        showResultModal = true
                    }
                },
                isLoading = isLoading
            )
        }
    }

    if (showResultModal) {
        ResultModal(
            isSuccess = resultIsSuccess,
            title = resultTitle,
            message = resultMessage,
            onDismiss = {
                showResultModal = false
                if (resultIsSuccess) {
                    selectedGame = null
                }
            },
            onRetry = if (!resultIsSuccess) {
                {
                    showResultModal = false
                }
            } else null
        )
    }
}