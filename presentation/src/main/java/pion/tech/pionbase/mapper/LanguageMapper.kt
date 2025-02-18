package pion.tech.pionbase.mapper

import com.piontech.domain.model.LanguageData
import pion.tech.pionbase.model.LanguageUIModel

fun LanguageData.toPresentation() = LanguageUIModel(
    thumbnail = this.thumbnail,
    nameCountry = this.nameCountry,
    localeCode = this.localeCode,
    isSelected = false
)