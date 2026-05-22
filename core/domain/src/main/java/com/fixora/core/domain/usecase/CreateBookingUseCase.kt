package com.fixora.core.domain.usecase

import com.fixora.core.domain.repository.BookingRepository
import com.fixora.core.model.Booking
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(
        userId: String,
        providerId: String,
        dateTime: String,
        timeSlot: String,
        issueDescription: String,
        attachmentUrl: String?
    ): Booking {
        return bookingRepository.createBooking(
            userId = userId,
            providerId = providerId,
            dateTime = dateTime,
            timeSlot = timeSlot,
            issueDescription = issueDescription,
            attachmentUrl = attachmentUrl
        )
    }
}
