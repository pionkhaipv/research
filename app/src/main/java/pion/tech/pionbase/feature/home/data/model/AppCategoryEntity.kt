package pion.tech.pionbase.feature.home.data.model

import pion.tech.pionbase.feature.home.domain.model.AppCategoryData


data class AppCategoryEntity(
    val id: String,
    val name: String,
)

fun AppCategoryEntity.toDomain(): AppCategoryData = AppCategoryData(id = this.id, name = this.name)