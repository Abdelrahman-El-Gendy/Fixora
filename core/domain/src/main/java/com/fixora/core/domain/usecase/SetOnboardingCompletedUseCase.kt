package com.fixora.core.domain.usecase

import com.fixora.core.domain.repository.UserDataRepository
import javax.inject.Inject

class SetOnboardingCompletedUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke() {
        userDataRepository.setOnboardingCompleted()
    }
}
