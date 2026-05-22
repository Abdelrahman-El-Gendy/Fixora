package com.fixora.core.data.repository

import com.fixora.core.domain.repository.AuthRepository
import com.fixora.core.model.User
import com.fixora.core.model.UserRole
import com.fixora.core.network.api.FixoraApi
import com.fixora.core.network.model.LoginRequest
import com.fixora.core.network.model.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: FixoraApi
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    override fun getAuthenticatedUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, password: String): User {
        val networkUser = api.login(LoginRequest(email, password))
        val domainUser = User(
            id = networkUser.id,
            name = networkUser.name,
            email = networkUser.email,
            profileImageUrl = networkUser.profileImageUrl,
            location = networkUser.location,
            role = UserRole.valueOf(networkUser.role)
        )
        _currentUser.value = domainUser
        return domainUser
    }

    override suspend fun register(name: String, email: String, password: String): User {
        val networkUser = api.register(RegisterRequest(name, email, password))
        val domainUser = User(
            id = networkUser.id,
            name = networkUser.name,
            email = networkUser.email,
            profileImageUrl = networkUser.profileImageUrl,
            location = networkUser.location,
            role = UserRole.valueOf(networkUser.role)
        )
        _currentUser.value = domainUser
        return domainUser
    }

    override suspend fun logout() {
        _currentUser.value = null
    }
}
