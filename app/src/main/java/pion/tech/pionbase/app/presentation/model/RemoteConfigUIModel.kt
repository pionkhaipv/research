package pion.tech.pionbase.app.presentation.model

import pion.tech.pionbase.app.domain.model.RemoteConfigData

data class RemoteConfigUIModel(
    val configShowAds: String,
    val admobId: String,
    val isRealData:Boolean
)

fun RemoteConfigData.toPresentation() =
    RemoteConfigUIModel(
        configShowAds = this.configShowAds,
        isRealData = this.isRealData,
        admobId = this.admobId,
    )