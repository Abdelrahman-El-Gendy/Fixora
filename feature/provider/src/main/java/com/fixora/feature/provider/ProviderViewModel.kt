package com.fixora.feature.provider

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixora.core.common.result.Result
import com.fixora.core.domain.usecase.GetProviderUseCase
import com.fixora.core.model.ServiceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProviderViewModel @Inject constructor(
    private val getProviderUseCase: GetProviderUseCase,
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
}
