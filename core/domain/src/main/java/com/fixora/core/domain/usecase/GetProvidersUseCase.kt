package com.fixora.core.domain.usecase

import com.fixora.core.common.result.Result
import com.fixora.core.common.result.asResult
import com.fixora.core.domain.repository.ServiceRepository
import com.fixora.core.model.ServiceProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProvidersUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    operator fun invoke(categoryId: String? = null, query: String? = null): Flow<Result<List<ServiceProvider>>> {
        return serviceRepository.getProviders(categoryId, query).asResult()
    }
}
