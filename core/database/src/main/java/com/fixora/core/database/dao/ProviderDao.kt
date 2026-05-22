package com.fixora.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fixora.core.database.model.ProviderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderDao {
    @Query("SELECT * FROM service_providers")
    fun getProviders(): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM service_providers WHERE categoryId = :categoryId")
    fun getProvidersByCategory(categoryId: String): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM service_providers WHERE name LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%'")
    fun searchProviders(query: String): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM service_providers WHERE id = :providerId")
    fun getProvider(providerId: String): Flow<ProviderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<ProviderEntity>)

    @Query("DELETE FROM service_providers")
    suspend fun clearProviders()
}
