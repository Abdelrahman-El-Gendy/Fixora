package com.fixora.core.domain.usecase

import com.fixora.core.domain.repository.AuthRepository
import com.fixora.core.model.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): User {
        return authRepository.login(email, password)
    }
}
