package pion.tech.pionbase.data.model.language

data class LanguageUIModel(
    val thumbnail: String,
    val nameCountry: String,
    val localeCode: String,
    var isSelected: Boolean,
)

fun LanguageDto.toPresentation() =
    LanguageUIModel(
        thumbnail = this.thumbnail,
        nameCountry = this.nameCountry,
        localeCode = this.localeCode,
        isSelected = false,
    )
