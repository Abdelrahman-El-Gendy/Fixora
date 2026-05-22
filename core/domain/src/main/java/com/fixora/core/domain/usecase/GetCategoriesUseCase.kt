package com.fixora.core.domain.usecase

import com.fixora.core.common.result.Result
import com.fixora.core.common.result.asResult
import com.fixora.core.domain.repository.ServiceRepository
import com.fixora.core.model.ServiceCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    operator fun invoke(): Flow<Result<List<ServiceCategory>>> {
        return serviceRepository.getCategories().asResult()
    }
}
