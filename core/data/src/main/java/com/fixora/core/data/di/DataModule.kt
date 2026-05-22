package com.fixora.core.data.di

import com.fixora.core.data.repository.AuthRepositoryImpl
import com.fixora.core.data.repository.BookingRepositoryImpl
import com.fixora.core.data.repository.ServiceRepositoryImpl
import com.fixora.core.domain.repository.AuthRepository
import com.fixora.core.domain.repository.BookingRepository
import com.fixora.core.domain.repository.ServiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(
        serviceRepositoryImpl: ServiceRepositoryImpl
    ): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository
}
