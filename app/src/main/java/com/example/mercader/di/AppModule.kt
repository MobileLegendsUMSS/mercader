package com.example.mercader.di

import android.content.ContentResolver
import android.content.Context
import com.example.mercader.data.local.ITokenRepository
import com.example.mercader.data.local.TokenRepository
import com.example.mercader.data.remote.AuthApi
import com.example.mercader.data.remote.apiservice.PaymentApiService
import com.example.mercader.domain.usecases.AuthenticationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(
        @ApplicationContext context: Context
    ): ITokenRepository {
        return TokenRepository(context)
    }

    @Provides
    @Singleton
    fun provideAuthenticationUseCase(
        tokenRepository: ITokenRepository
    ): AuthenticationUseCase {
        return AuthenticationUseCase(tokenRepository)
    }

    @Provides
    @Singleton
    fun providePaymentApiService(retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver {
        return context.contentResolver
    }

}