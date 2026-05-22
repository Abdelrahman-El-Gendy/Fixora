package com.fixora.core.data.repository

import com.fixora.core.database.dao.CategoryDao
import com.fixora.core.database.dao.ProviderDao
import com.fixora.core.database.model.CategoryEntity
import com.fixora.core.database.model.ProviderEntity
import com.fixora.core.database.model.CachedReview
import com.fixora.core.database.model.toDomain
import com.fixora.core.domain.repository.ServiceRepository
import com.fixora.core.model.ServiceCategory
import com.fixora.core.model.ServiceProvider
import com.fixora.core.network.api.FixoraApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepositoryImpl @Inject constructor(
    private val api: FixoraApi,
    private val categoryDao: CategoryDao,
    private val providerDao: ProviderDao
) : ServiceRepository {

    override fun getCategories(): Flow<List<ServiceCategory>> {
        return categoryDao.getCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getProviders(categoryId: String?, query: String?): Flow<List<ServiceProvider>> {
        val flow = when {
            !query.isNullOrBlank() -> providerDao.searchProviders(query)
            !categoryId.isNullOrBlank() -> providerDao.getProvidersByCategory(categoryId)
            else -> providerDao.getProviders()
        }
        return flow.map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getProvider(providerId: String): Flow<ServiceProvider?> {
        return providerDao.getProvider(providerId).map { it?.toDomain() }
    }

    override suspend fun refreshCategories() {
        val networkCategories = api.getCategories()
        val entities = networkCategories.map {
            CategoryEntity(
                id = it.id,
                name = it.name,
                description = it.description,
                iconName = it.iconName
            )
        }
        categoryDao.clearCategories()
        categoryDao.insertCategories(entities)
    }

    override suspend fun refreshProviders() {
        val networkProviders = api.getProviders()
        val entities = networkProviders.map { p ->
            ProviderEntity(
                id = p.id,
                name = p.name,
                title = p.title,
                categoryId = p.categoryId,
                rating = p.rating,
                reviewCount = p.reviewCount,
                pricePerHour = p.pricePerHour,
                bio = p.bio,
                avatarUrl = p.avatarUrl,
                workImages = p.workImages,
                availableSlots = p.availableSlots,
                reviews = p.reviews.map { r ->
                    CachedReview(
                        id = r.id,
                        reviewerName = r.reviewerName,
                        reviewerAvatarUrl = r.reviewerAvatarUrl,
                        rating = r.rating,
                        comment = r.comment,
                        date = r.date
                    )
                }
            )
        }
        providerDao.clearProviders()
        providerDao.insertProviders(entities)
    }
}
