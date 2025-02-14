package pion.tech.pionbase.framework.presentation.mapper

import com.piontech.domain.model.Language
import pion.tech.pionbase.framework.presentation.model.LanguageUIModel

fun Language.toPresentation() = LanguageUIModel(
    thumbnail = this.thumbnail,
    nameCountry = this.nameCountry,
    localeCode = this.localeCode,
    isSelected = false
)