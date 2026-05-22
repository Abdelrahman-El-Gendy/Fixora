package com.fixora.core.network.api

import com.fixora.core.network.model.*
import retrofit2.http.*

interface FixoraApi {
    @GET("categories")
    suspend fun getCategories(): List<NetworkServiceCategory>

    @GET("providers")
    suspend fun getProviders(
        @Query("categoryId") categoryId: String? = null,
        @Query("q") query: String? = null
    ): List<NetworkServiceProvider>

    @GET("providers/{id}")
    suspend fun getProvider(@Path("id") providerId: String): NetworkServiceProvider

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): NetworkUser

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): NetworkUser

    @GET("bookings")
    suspend fun getBookings(@Query("userId") userId: String): List<NetworkBooking>

    @POST("bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): NetworkBooking
}
