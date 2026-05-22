package com.fixora.core.domain.repository

import com.fixora.core.model.ServiceCategory
import com.fixora.core.model.ServiceProvider
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getCategories(): Flow<List<ServiceCategory>>
    fun getProviders(categoryId: String? = null, query: String? = null): Flow<List<ServiceProvider>>
    fun getProvider(providerId: String): Flow<ServiceProvider?>
    suspend fun refreshCategories()
    suspend fun refreshProviders()
}
