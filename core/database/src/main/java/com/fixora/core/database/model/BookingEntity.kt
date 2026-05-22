package com.fixora.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fixora.core.model.Booking
import com.fixora.core.model.BookingStatus

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val providerId: String,
    val providerName: String,
    val providerAvatarUrl: String?,
    val categoryName: String,
    val dateTime: String,
    val timeSlot: String,
    val status: String,
    val issueDescription: String,
    val attachmentUrl: String?,
    val totalPrice: Double
)

fun BookingEntity.toDomain() = Booking(
    id = id,
    userId = userId,
    providerId = providerId,
    providerName = providerName,
    providerAvatarUrl = providerAvatarUrl,
    categoryName = categoryName,
    dateTime = dateTime,
    timeSlot = timeSlot,
    status = BookingStatus.valueOf(status),
    issueDescription = issueDescription,
    attachmentUrl = attachmentUrl,
    totalPrice = totalPrice
)

fun Booking.toEntity() = BookingEntity(
    id = id,
    userId = userId,
    providerId = providerId,
    providerName = providerName,
    providerAvatarUrl = providerAvatarUrl,
    categoryName = categoryName,
    dateTime = dateTime,
    timeSlot = timeSlot,
    status = status.name,
    issueDescription = issueDescription,
    attachmentUrl = attachmentUrl,
    totalPrice = totalPrice
)
