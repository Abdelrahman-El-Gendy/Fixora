package com.fixora.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fixora.core.database.dao.BookingDao
import com.fixora.core.database.dao.CategoryDao
import com.fixora.core.database.dao.ProviderDao
import com.fixora.core.database.model.BookingEntity
import com.fixora.core.database.model.CategoryEntity
import com.fixora.core.database.model.ProviderEntity
import com.fixora.core.database.util.ListConverter
import com.fixora.core.database.model.ReviewsConverter

@Database(
    entities = [
        CategoryEntity::class,
        ProviderEntity::class,
        BookingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class, ReviewsConverter::class)
abstract class FixoraDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun providerDao(): ProviderDao
    abstract fun bookingDao(): BookingDao
}
