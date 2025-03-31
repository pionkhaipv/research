package pion.tech.pionbase.language.presentation.mapper

import pion.tech.pionbase.language.domain.model.LanguageData
import pion.tech.pionbase.language.presentation.model.LanguageUIModel

fun LanguageData.toPresentation() = LanguageUIModel(
    thumbnail = this.thumbnail,
    nameCountry = this.nameCountry,
    localeCode = this.localeCode,
    isSelected = false
)