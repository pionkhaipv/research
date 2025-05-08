package pion.tech.pionbase.feature.home.presetation.model

import pion.tech.pionbase.feature.home.domain.model.template.TemplateData

data class TemplateUIModel(
    val name: String? = null,
    val duration: String? = null,
    val videoPreview: String? = null,
    val aspectRatio: String? = null,
    val music: String? = null,
    val thumbnail: String? = null,
    val templateType: String? = null,
    val imageModel: String? = null,
    val categoryId: String? = null,
    val countryId: String? = null
)

fun TemplateData.toPresentation() =
    TemplateUIModel(
        name = this.name,
        duration = this.duration,
        videoPreview = this.videoPreview,
        aspectRatio = this.aspectRatio,
        music = this.music,
        thumbnail = this.thumbnail,
        templateType = this.templateType,
        imageModel = this.imageModel,
        categoryId = this.categoryId,
        countryId = this.countryId

    )