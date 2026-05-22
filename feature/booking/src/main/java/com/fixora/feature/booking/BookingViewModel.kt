package com.fixora.feature.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixora.core.common.result.Result
import com.fixora.core.domain.usecase.CreateBookingUseCase
import com.fixora.core.domain.usecase.GetProviderUseCase
import com.fixora.core.model.Booking
import com.fixora.core.model.ServiceProvider
import com.fixora.core.domain.usecase.GetAuthenticatedUserUseCase
import com.fixora.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BookingUiState {
    object Idle : BookingUiState
    object Loading : BookingUiState
    data class Success(val booking: Booking) : BookingUiState
    data class Error(val message: String) : BookingUiState
}

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val getProviderUseCase: GetProviderUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val providerId: String = checkNotNull(savedStateHandle["providerId"]) {
        "providerId is required in savedStateHandle"
    }

    val providerState: StateFlow<Result<ServiceProvider?>> = getProviderUseCase(providerId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    val userState: StateFlow<User?> = getAuthenticatedUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _bookingState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingState: StateFlow<BookingUiState> = _bookingState.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedTimeSlot = MutableStateFlow("")
    val selectedTimeSlot: StateFlow<String> = _selectedTimeSlot.asStateFlow()

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun selectTimeSlot(slot: String) {
        _selectedTimeSlot.value = slot
    }

    fun submitBooking(issueDescription: String, attachmentUrl: String? = null) {
        val date = _selectedDate.value
        val slot = _selectedTimeSlot.value
        val userId = userState.value?.id

        if (userId == null) {
            _bookingState.value = BookingUiState.Error("User not logged in.")
            return
        }

        if (date.isBlank() || slot.isBlank() || issueDescription.isBlank()) {
            _bookingState.value = BookingUiState.Error("Please select a date, time, and describe the issue.")
            return
        }

        viewModelScope.launch {
            _bookingState.value = BookingUiState.Loading
            try {
                val booking = createBookingUseCase(
                    userId = userId,
                    providerId = providerId,
                    dateTime = date,
                    timeSlot = slot,
                    issueDescription = issueDescription,
                    attachmentUrl = attachmentUrl
                )
                _bookingState.value = BookingUiState.Success(booking)
            } catch (e: Exception) {
                _bookingState.value = BookingUiState.Error(e.message ?: "Booking transaction failed")
            }
        }
    }

    fun resetState() {
        _bookingState.value = BookingUiState.Idle
    }
}
