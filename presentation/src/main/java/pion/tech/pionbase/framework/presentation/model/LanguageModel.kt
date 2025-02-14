package pion.tech.pionbase.framework.presentation.model

data class LanguageModel(
    val thumbnail: String,
    val nameCountry: String,
    val localeCode: String,
    var isSelected: Boolean
)