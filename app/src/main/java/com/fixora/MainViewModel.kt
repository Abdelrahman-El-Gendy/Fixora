package com.fixora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixora.core.domain.repository.AuthRepository
import com.fixora.core.domain.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface MainUiState {
    object Loading : MainUiState
    data class Success(
        val shouldShowOnboarding: Boolean,
        val isAuthenticated: Boolean
    ) : MainUiState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = combine(
        userDataRepository.shouldShowOnboarding,
        authRepository.getAuthenticatedUser()
    ) { shouldShowOnboarding, user ->
        MainUiState.Success(
            shouldShowOnboarding = shouldShowOnboarding,
            isAuthenticated = user != null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainUiState.Loading
    )
}
