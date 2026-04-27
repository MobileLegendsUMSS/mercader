package com.example.mercader.di

import com.example.mercader.data.repositories.GameRepositoryImpl
import com.example.mercader.data.repositories.ReservationRepositoryImpl
import com.example.mercader.data.repositories.RentalRepositoryImpl
import com.example.mercader.domain.repositories.GameRepository
import com.example.mercader.domain.repositories.ReservationRepository
import com.example.mercader.domain.repositories.RentalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        gameRepositoryImpl: GameRepositoryImpl
    ): GameRepository

    @Binds
    @Singleton
    abstract fun bindReservationRepository(
        reservationRepositoryImpl: ReservationRepositoryImpl
    ): ReservationRepository

    @Binds
    @Singleton
    abstract fun bindRentalRepository(
        rentalRepositoryImpl: RentalRepositoryImpl
    ): RentalRepository
}