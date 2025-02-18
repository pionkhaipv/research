package pion.tech.pionbase.mapper

import com.piontech.domain.model.TemplateData
import pion.tech.pionbase.model.TemplateUIModel

fun TemplateData.toPresentation() = TemplateUIModel(
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