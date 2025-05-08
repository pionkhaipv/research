package pion.tech.pionbase.feature.home.presetation.model

import pion.tech.pionbase.feature.home.domain.model.AppCategoryData


data class AppCategoryUIModel(
    val id: String,
    val name: String
)

fun AppCategoryData.toPresentation(): AppCategoryUIModel =
    AppCategoryUIModel(id = this.id, name = this.name)