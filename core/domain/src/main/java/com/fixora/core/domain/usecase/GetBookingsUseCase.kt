package com.fixora.core.domain.usecase

import com.fixora.core.common.result.Result
import com.fixora.core.common.result.asResult
import com.fixora.core.domain.repository.BookingRepository
import com.fixora.core.model.Booking
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookingsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    operator fun invoke(userId: String): Flow<Result<List<Booking>>> {
        return bookingRepository.getBookings(userId).asResult()
    }
}
