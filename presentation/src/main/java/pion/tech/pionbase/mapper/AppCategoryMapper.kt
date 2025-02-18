package pion.tech.pionbase.mapper

import com.piontech.domain.model.AppCategoryData
import com.piontech.domain.model.LanguageData
import pion.tech.pionbase.model.AppCategoryUIModel
import pion.tech.pionbase.model.LanguageUIModel

fun AppCategoryData.toPresentation(): AppCategoryUIModel =
    AppCategoryUIModel(id = this.id, name = this.name)