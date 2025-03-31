package pion.tech.pionbase.home.presetation.mapper

import pion.tech.pionbase.home.domain.model.AppCategoryData
import pion.tech.pionbase.model.AppCategoryUIModel

fun AppCategoryData.toPresentation(): AppCategoryUIModel =
    AppCategoryUIModel(id = this.id, name = this.name)