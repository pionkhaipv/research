package pion.tech.pionbase.feature.language.presentation.model

import pion.tech.pionbase.feature.language.domain.model.LanguageData


data class LanguageUIModel(
    val thumbnail: String,
    val nameCountry: String,
    val localeCode: String,
    var isSelected: Boolean
)

fun LanguageData.toPresentation() = LanguageUIModel(
    thumbnail = this.thumbnail,
    nameCountry = this.nameCountry,
    localeCode = this.localeCode,
    isSelected = false
)