package com.fixora.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.fixora.core.model.Review
import com.fixora.core.model.ServiceProvider
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class CachedReview(
    val id: String,
    val reviewerName: String,
    val reviewerAvatarUrl: String? = null,
    val rating: Float,
    val comment: String,
    val date: String
)

fun CachedReview.toDomain() = Review(
    id = id,
    reviewerName = reviewerName,
    reviewerAvatarUrl = reviewerAvatarUrl,
    rating = rating,
    comment = comment,
    date = date
)

fun Review.toCached() = CachedReview(
    id = id,
    reviewerName = reviewerName,
    reviewerAvatarUrl = reviewerAvatarUrl,
    rating = rating,
    comment = comment,
    date = date
)

@Entity(tableName = "service_providers")
data class ProviderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val title: String,
    val categoryId: String,
    val rating: Float,
    val reviewCount: Int,
    val pricePerHour: Double,
    val bio: String,
    val avatarUrl: String?,
    val workImages: List<String>,
    val reviews: List<CachedReview>,
    val availableSlots: List<String>
)

fun ProviderEntity.toDomain() = ServiceProvider(
    id = id,
    name = name,
    title = title,
    categoryId = categoryId,
    rating = rating,
    reviewCount = reviewCount,
    pricePerHour = pricePerHour,
    bio = bio,
    avatarUrl = avatarUrl,
    workImages = workImages,
    reviews = reviews.map { it.toDomain() },
    availableSlots = availableSlots
)

fun ServiceProvider.toEntity() = ProviderEntity(
    id = id,
    name = name,
    title = title,
    categoryId = categoryId,
    rating = rating,
    reviewCount = reviewCount,
    pricePerHour = pricePerHour,
    bio = bio,
    avatarUrl = avatarUrl,
    workImages = workImages,
    reviews = reviews.map { it.toCached() },
    availableSlots = availableSlots
)

class ReviewsConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromReviewList(value: List<CachedReview>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toReviewList(value: String): List<CachedReview> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
