package com.fixora.core.domain.repository

import com.fixora.core.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getAuthenticatedUser(): Flow<User?>
    suspend fun login(email: String, password: String): User
    suspend fun register(name: String, email: String, password: String): User
    suspend fun logout()
}
