package com.fixora.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val shouldShowOnboarding: Flow<Boolean>
    suspend fun setOnboardingCompleted()
}
