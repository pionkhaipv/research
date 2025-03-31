package pion.tech.pionbase.language.presentation.model

data class LanguageUIModel(
    val thumbnail: String,
    val nameCountry: String,
    val localeCode: String,
    var isSelected: Boolean
)