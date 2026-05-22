package com.fixora.core.database.di

import android.content.Context
import androidx.room.Room
import com.fixora.core.database.FixoraDatabase
import com.fixora.core.database.dao.BookingDao
import com.fixora.core.database.dao.CategoryDao
import com.fixora.core.database.dao.ProviderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FixoraDatabase {
        return Room.databaseBuilder(
            context,
            FixoraDatabase::class.java,
            "fixora_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideCategoryDao(database: FixoraDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideProviderDao(database: FixoraDatabase): ProviderDao = database.providerDao()

    @Provides
    fun provideBookingDao(database: FixoraDatabase): BookingDao = database.bookingDao()
}
