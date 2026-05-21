package com.example.mercader.di

import android.content.Context
import com.example.mercader.common.utils.CartManager
import com.example.mercader.data.repositories.CartRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    @Singleton
    fun provideCartManager(
        @ApplicationContext context: Context,
        cartRepository: CartRepository  // Esto ahora viene de NetworkModule
    ): CartManager {
        return CartManager(context, cartRepository)
    }
}