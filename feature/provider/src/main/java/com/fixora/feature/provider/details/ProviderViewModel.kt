package com.fixora.feature.provider.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixora.core.common.result.Result
import com.fixora.core.domain.usecase.GetProviderUseCase
import com.fixora.core.model.ServiceProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = ProviderViewModel.Factory::class)
class ProviderViewModel @AssistedInject constructor(
    @Assisted val providerId: String,
    private val getProviderUseCase: GetProviderUseCase
) : ViewModel() {

    val providerState: StateFlow<Result<ServiceProvider?>> = getProviderUseCase(providerId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    @AssistedFactory
    interface Factory {
        fun create(providerId: String): ProviderViewModel
    }
}
