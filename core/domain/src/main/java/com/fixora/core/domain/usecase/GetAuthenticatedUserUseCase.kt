package com.fixora.core.domain.usecase

import com.fixora.core.domain.repository.AuthRepository
import com.fixora.core.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthenticatedUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.getAuthenticatedUser()
    }
}
