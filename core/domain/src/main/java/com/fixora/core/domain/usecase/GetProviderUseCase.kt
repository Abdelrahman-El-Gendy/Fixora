package com.fixora.core.domain.usecase

import com.fixora.core.common.result.Result
import com.fixora.core.common.result.asResult
import com.fixora.core.domain.repository.ServiceRepository
import com.fixora.core.model.ServiceProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProviderUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    operator fun invoke(providerId: String): Flow<Result<ServiceProvider?>> {
        return serviceRepository.getProvider(providerId).asResult()
    }
}
