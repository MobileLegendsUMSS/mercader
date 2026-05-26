package com.example.mercader.ui.screens.profile

import com.example.mercader.domain.models.Game
import com.example.mercader.domain.models.UserPurchase
import com.example.mercader.domain.models.UserLoan

data class ProfileState(
    val username: String = "",
    val name: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val mercaPoints: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val favorites: List<Game> = emptyList(),
    val isFavoritesLoading: Boolean = false,
    val favoritesError: String? = null,
    val purchases: List<UserPurchase> = emptyList(),
    val isPurchasesLoading: Boolean = false,
    val purchasesError: String? = null,
    val loans: List<UserLoan> = emptyList(),
    val isLoansLoading: Boolean = false,
    val loansError: String? = null
)
