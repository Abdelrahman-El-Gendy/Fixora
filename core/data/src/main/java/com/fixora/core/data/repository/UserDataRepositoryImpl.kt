package com.fixora.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.fixora.core.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserDataRepository {

    private object PreferencesKeys {
        val SHOULD_SHOW_ONBOARDING = booleanPreferencesKey("should_show_onboarding")
    }

    override val shouldShowOnboarding: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SHOULD_SHOW_ONBOARDING] ?: true
        }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOULD_SHOW_ONBOARDING] = false
        }
    }
}
