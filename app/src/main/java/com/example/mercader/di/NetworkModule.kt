package com.example.mercader.di

import com.example.mercader.common.constants.AppConstants
import com.example.mercader.data.remote.apiservice.CartApiService
import com.example.mercader.data.remote.apiservice.GameApiService
import com.example.mercader.data.remote.apiservice.UserApiService
import com.example.mercader.data.remote.apiservice.ReserveApiService
import com.example.mercader.data.repositories.CartRepository
import com.example.mercader.data.repositories.UserRepositoryImpl
import com.example.mercader.data.repository.CartRepositoryImpl
import com.example.mercader.domain.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

import com.example.mercader.data.remote.apiservice.AuthApiService

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGameApiService(retrofit: Retrofit): GameApiService {
        return retrofit.create(GameApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCartApiService(retrofit: Retrofit): CartApiService {
        return retrofit.create(CartApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        apiService: CartApiService
    ): CartRepository {
        return CartRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: UserApiService,
        tokenRepository: com.example.mercader.data.local.ITokenRepository
    ): UserRepository {
        return UserRepositoryImpl(apiService, tokenRepository)
    }
    @Provides
    @Singleton
    fun provideReserveService(retrofit: Retrofit): ReserveApiService {
        return retrofit.create(ReserveApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}

