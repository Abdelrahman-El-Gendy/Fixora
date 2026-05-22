package com.fixora.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fixora.core.model.ServiceCategory

@Entity(tableName = "service_categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String
)

fun CategoryEntity.toDomain() = ServiceCategory(
    id = id,
    name = name,
    description = description,
    iconName = iconName
)

fun ServiceCategory.toEntity() = CategoryEntity(
    id = id,
    name = name,
    description = description,
    iconName = iconName
)
