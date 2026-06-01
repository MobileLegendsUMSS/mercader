package com.example.mercader.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.common.utils.ReviewManager
import com.example.mercader.domain.models.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewManager: ReviewManager
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewUiState())
    val state: StateFlow<ReviewUiState> = _state.asStateFlow()

    data class ReviewUiState(
        val reviews: List<Review> = emptyList(),
        val isLoading: Boolean = false,
        val isCreating: Boolean = false,
        val errorMessage: String? = null,
        val sortByNewest: Boolean = true  // true = más recientes, false = más antiguos
    )

    fun loadReviews(gameId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val reviews = reviewManager.getReviewsByGame(gameId)
                val sortedReviews = if (_state.value.sortByNewest) {
                    reviews.sortedByDescending { it.timestamp }
                } else {
                    reviews.sortedBy { it.timestamp }
                }

                _state.value = _state.value.copy(
                    reviews = sortedReviews,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar reseñas"
                )
            }
        }
    }

    fun toggleSortOrder() {
        val newSortOrder = !_state.value.sortByNewest
        val sortedReviews = if (newSortOrder) {
            _state.value.reviews.sortedByDescending { it.timestamp }
        } else {
            _state.value.reviews.sortedBy { it.timestamp }
        }

        _state.value = _state.value.copy(
            sortByNewest = newSortOrder,
            reviews = sortedReviews
        )
    }

    fun createReview(gameId: String, content: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCreating = true, errorMessage = null)

            try {
                val success = reviewManager.createReview(gameId, content)
                if (success) {
                    // Recargar reseñas después de crear una nueva
                    loadReviews(gameId)
                    onSuccess()
                } else {
                    _state.value = _state.value.copy(
                        isCreating = false,
                        errorMessage = "No se pudo crear la reseña. Intenta nuevamente."
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isCreating = false,
                    errorMessage = e.message ?: "Error al crear la reseña"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}