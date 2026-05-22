package com.example.mercader.di

import android.content.Context
import com.example.mercader.common.utils.ReserveManager
import com.example.mercader.data.remote.apiservice.ReserveApiService
import com.example.mercader.data.repository.ReserveRepositoryImpl
import com.example.mercader.domain.repositories.ReserveRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReserveModule {

    @Provides
    @Singleton
    fun provideReserveRepository(
        apiService: ReserveApiService
    ): ReserveRepository {
        return ReserveRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideReserveManager(
        ReserveRepository: ReserveRepository
    ): ReserveManager {
        return ReserveManager(ReserveRepository)
    }
}