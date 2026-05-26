package com.example.mercader.domain.models

data class UserLoan(
    val loanId: String,
    val title: String,
    val description: String,
    val service: String,
    val requestDate: String,
    val limitDate: String,
    val startDate: String?,
    val endDate: String?
)
