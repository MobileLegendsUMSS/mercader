package com.example.mercader.di

import com.example.mercader.data.remote.apiservice.GameApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.mercader.common.constants.Env
val retrofit = Retrofit.Builder()
    .baseUrl(Env.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(GameApiService::class.java)