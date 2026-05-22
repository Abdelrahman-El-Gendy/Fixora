package com.fixora.core.data.repository

import com.fixora.core.database.dao.BookingDao
import com.fixora.core.database.model.BookingEntity
import com.fixora.core.database.model.toDomain
import com.fixora.core.domain.repository.BookingRepository
import com.fixora.core.model.Booking
import com.fixora.core.model.BookingStatus
import com.fixora.core.network.api.FixoraApi
import com.fixora.core.network.model.CreateBookingRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val api: FixoraApi,
    private val bookingDao: BookingDao
) : BookingRepository {

    override fun getBookings(userId: String): Flow<List<Booking>> {
        return bookingDao.getBookings(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBooking(bookingId: String): Flow<Booking?> {
        return bookingDao.getBooking(bookingId).map { it?.toDomain() }
    }

    override suspend fun createBooking(
        userId: String,
        providerId: String,
        dateTime: String,
        timeSlot: String,
        issueDescription: String,
        attachmentUrl: String?
    ): Booking {
        val request = CreateBookingRequest(
            userId = userId,
            providerId = providerId,
            dateTime = dateTime,
            timeSlot = timeSlot,
            issueDescription = issueDescription,
            attachmentUrl = attachmentUrl
        )
        val networkBooking = api.createBooking(request)

        val entity = BookingEntity(
            id = networkBooking.id,
            userId = networkBooking.userId,
            providerId = networkBooking.providerId,
            providerName = networkBooking.providerName,
            providerAvatarUrl = networkBooking.providerAvatarUrl,
            categoryName = networkBooking.categoryName,
            dateTime = networkBooking.dateTime,
            timeSlot = networkBooking.timeSlot,
            status = networkBooking.status,
            issueDescription = networkBooking.issueDescription,
            attachmentUrl = networkBooking.attachmentUrl,
            totalPrice = networkBooking.totalPrice
        )

        bookingDao.insertBooking(entity)
        return entity.toDomain()
    }

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        bookingDao.updateBookingStatus(bookingId, status.name)
    }
}
