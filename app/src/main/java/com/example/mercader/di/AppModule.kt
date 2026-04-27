package com.example.mercader.di

import com.example.mercader.data.remote.apiservice.GameApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.mercader.common.constants.AppConstants
val retrofit = Retrofit.Builder()
    .baseUrl(AppConstants.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(GameApiService::class.java)