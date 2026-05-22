package com.fixora.core.model

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}

data class Booking(
    val id: String,
    val userId: String,
    val providerId: String,
    val providerName: String,
    val providerAvatarUrl: String?,
    val categoryName: String,
    val dateTime: String,
    val timeSlot: String,
    val status: BookingStatus,
    val issueDescription: String,
    val attachmentUrl: String?,
    val totalPrice: Double
)
