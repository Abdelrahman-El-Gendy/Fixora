package com.fixora.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

@Serializable
data object LoginRoute

@Serializable
data object RegisterRoute

@Serializable
data object HomeRoute

@Serializable
data class ProviderDetailRoute(val providerId: String)

@Serializable
data object ProviderListRoute

@Serializable
data object ProviderDashboardRoute

@Serializable
data class BookingRoute(val providerId: String)

@Serializable
data object ProfileRoute
@Serializable
data object OnboardingRoute