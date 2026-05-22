package com.fixora.core.model

data class ServiceProvider(
    val id: String,
    val name: String,
    val title: String,
    val categoryId: String,
    val rating: Float,
    val reviewCount: Int,
    val pricePerHour: Double,
    val bio: String,
    val avatarUrl: String?,
    val workImages: List<String>,
    val reviews: List<Review>,
    val availableSlots: List<String> // list of time slots like "09:00 AM", "11:00 AM", "02:00 PM"
)
