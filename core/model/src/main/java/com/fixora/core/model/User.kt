package com.fixora.core.model

enum class UserRole {
    CUSTOMER,
    PROVIDER
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String?,
    val location: String?,
    val role: UserRole
)
