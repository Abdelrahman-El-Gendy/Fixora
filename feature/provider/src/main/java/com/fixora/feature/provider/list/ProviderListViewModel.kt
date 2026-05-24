package com.fixora.feature.provider.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixora.core.common.result.Result
import com.fixora.core.domain.usecase.GetProvidersUseCase
import com.fixora.core.model.ServiceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProviderListViewModel @Inject constructor(
    private val getProvidersUseCase: GetProvidersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Filters")
    val selectedFilter = _selectedFilter.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val providers: StateFlow<Result<List<ServiceProvider>>> = _searchQuery
        .flatMapLatest { query ->
            getProvidersUseCase(query = query.ifBlank { null })
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }
}
