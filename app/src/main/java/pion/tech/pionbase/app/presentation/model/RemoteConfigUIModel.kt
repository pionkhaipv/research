package pion.tech.pionbase.app.presentation.model

import pion.tech.pionbase.app.data.model.RemoteConfigDto

data class RemoteConfigUIModel(
    val configShowAds: String,
    val admobId: String,
    val isRealData: Boolean,
)

fun RemoteConfigDto.toPresentation() =
    RemoteConfigUIModel(
        configShowAds = this.configShowAds,
        isRealData = this.isRealData,
        admobId = this.admobId,
    )
