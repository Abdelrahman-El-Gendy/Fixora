package com.fixora.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixora.core.common.result.Result
import com.fixora.core.domain.repository.ServiceRepository
import com.fixora.core.domain.usecase.GetCategoriesUseCase
import com.fixora.core.domain.usecase.GetProvidersUseCase
import com.fixora.core.model.ServiceCategory
import com.fixora.core.model.ServiceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProvidersUseCase: GetProvidersUseCase,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val categories: StateFlow<Result<List<ServiceCategory>>> = getCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val providers: StateFlow<Result<List<ServiceProvider>>> = combine(
        _selectedCategoryId,
        _searchQuery
    ) { categoryId, query ->
        Pair(categoryId, query)
    }.flatMapLatest { (categoryId, query) ->
        getProvidersUseCase(categoryId, query.ifBlank { null })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading
    )

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                serviceRepository.refreshCategories()
                serviceRepository.refreshProviders()
            } catch (e: Exception) {
                // Offline fallback will show local data
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    fun search(query: String) {
        _searchQuery.value = query
    }
}
