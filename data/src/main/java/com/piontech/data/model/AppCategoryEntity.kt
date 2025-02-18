package com.piontech.data.model

import com.piontech.domain.model.AppCategoryData

data class AppCategoryEntity(
    val id: String,
    val name: String,
)

fun AppCategoryEntity.toDomain(): AppCategoryData = AppCategoryData(id = this.id, name = this.name)