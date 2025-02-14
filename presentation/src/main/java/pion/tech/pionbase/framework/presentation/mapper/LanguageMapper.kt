package pion.tech.pionbase.framework.presentation.mapper

import com.piontech.domain.model.LanguageData
import pion.tech.pionbase.framework.presentation.model.LanguageModel

fun LanguageData.toPresentation() = LanguageModel(
    thumbnail = this.thumbnail,
    nameCountry = this.nameCountry,
    localeCode = this.localeCode,
    isSelected = false
)