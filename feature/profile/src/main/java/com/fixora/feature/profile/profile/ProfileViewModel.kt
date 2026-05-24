package com.fixora.feature.profile.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixora.core.common.result.Result
import com.fixora.core.domain.usecase.GetAuthenticatedUserUseCase
import com.fixora.core.domain.usecase.GetBookingsUseCase
import com.fixora.core.domain.usecase.LogoutUseCase
import com.fixora.core.model.Booking
import com.fixora.core.model.BookingStatus
import com.fixora.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BookingTab(val label: String) {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val getBookingsUseCase: GetBookingsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    val userState: StateFlow<User?> = getAuthenticatedUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val bookingsState: StateFlow<Result<List<Booking>>> = getAuthenticatedUserUseCase()
        .flatMapLatest { user ->
            if (user != null) {
                getBookingsUseCase(user.id)
            } else {
                flowOf(Result.Success(emptyList()))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    private val _selectedTab = MutableStateFlow(BookingTab.ACTIVE)
    val selectedTab: StateFlow<BookingTab> = _selectedTab.asStateFlow()

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null) // null = follow system
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    fun selectTab(tab: BookingTab) {
        _selectedTab.value = tab
    }

    fun toggleTheme(dark: Boolean) {
        _isDarkTheme.value = dark
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun getFilteredBookings(bookings: List<Booking>, tab: BookingTab): List<Booking> {
        return when (tab) {
            BookingTab.ACTIVE -> bookings.filter {
                it.status == BookingStatus.PENDING || it.status == BookingStatus.CONFIRMED
            }
            BookingTab.COMPLETED -> bookings.filter { it.status == BookingStatus.COMPLETED }
            BookingTab.CANCELLED -> bookings.filter { it.status == BookingStatus.CANCELLED }
        }
    }
}
