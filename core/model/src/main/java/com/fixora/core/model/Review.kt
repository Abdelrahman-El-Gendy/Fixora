package com.fixora.core.model

data class Review(
    val id: String,
    val reviewerName: String,
    val reviewerAvatarUrl: String?,
    val rating: Float,
    val comment: String,
    val date: String
)
