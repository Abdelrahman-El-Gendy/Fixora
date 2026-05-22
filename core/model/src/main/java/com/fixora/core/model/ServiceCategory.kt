package com.fixora.core.model

data class ServiceCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String // Name of the vector icon (e.g., "plumbing", "electrical")
)
