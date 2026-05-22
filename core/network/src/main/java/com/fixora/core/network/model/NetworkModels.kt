package com.fixora.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkUser(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null,
    val location: String? = null,
    val role: String
)

@Serializable
data class NetworkServiceCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String
)

@Serializable
data class NetworkReview(
    val id: String,
    val reviewerName: String,
    val reviewerAvatarUrl: String? = null,
    val rating: Float,
    val comment: String,
    val date: String
)

@Serializable
data class NetworkServiceProvider(
    val id: String,
    val name: String,
    val title: String,
    val categoryId: String,
    val rating: Float,
    val reviewCount: Int,
    val pricePerHour: Double,
    val bio: String,
    val avatarUrl: String? = null,
    val workImages: List<String> = emptyList(),
    val reviews: List<NetworkReview> = emptyList(),
    val availableSlots: List<String> = emptyList()
)

@Serializable
data class NetworkBooking(
    val id: String,
    val userId: String,
    val providerId: String,
    val providerName: String,
    val providerAvatarUrl: String? = null,
    val categoryName: String,
    val dateTime: String,
    val timeSlot: String,
    val status: String,
    val issueDescription: String,
    val attachmentUrl: String? = null,
    val totalPrice: Double
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class CreateBookingRequest(
    val userId: String,
    val providerId: String,
    val dateTime: String,
    val timeSlot: String,
    val issueDescription: String,
    val attachmentUrl: String? = null
)
