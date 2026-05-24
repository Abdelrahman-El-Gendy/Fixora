package com.fixora.feature.provider.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixora.core.domain.repository.AuthRepository
import com.fixora.core.domain.repository.BookingRepository
import com.fixora.core.model.Booking
import com.fixora.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProviderDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val userState: StateFlow<User?> = authRepository.getAuthenticatedUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _isOnline = MutableStateFlow(true)
    val isOnline = _isOnline.asStateFlow()

    // Mocking incoming requests for now as we don't have a specific provider bookings flow
    val incomingRequests: StateFlow<List<Booking>> = authRepository.getAuthenticatedUser()
        .map { user: User? ->
            if (user == null) emptyList<Booking>()
            else {
                // In a real app, we'd call bookingRepository.getBookingsForProvider(user.id)
                emptyList<Booking>()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList<Booking>()
        )

    fun toggleOnlineStatus(online: Boolean) {
        _isOnline.value = online
    }
}
