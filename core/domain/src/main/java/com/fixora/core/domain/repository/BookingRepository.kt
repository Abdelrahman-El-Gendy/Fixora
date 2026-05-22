package com.fixora.core.domain.repository

import com.fixora.core.model.Booking
import com.fixora.core.model.BookingStatus
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getBookings(userId: String): Flow<List<Booking>>
    fun getBooking(bookingId: String): Flow<Booking?>
    suspend fun createBooking(
        userId: String,
        providerId: String,
        dateTime: String,
        timeSlot: String,
        issueDescription: String,
        attachmentUrl: String?
    ): Booking
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus)
}
