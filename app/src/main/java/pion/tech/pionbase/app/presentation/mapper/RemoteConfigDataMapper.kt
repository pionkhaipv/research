package pion.tech.pionbase.app.presentation.mapper

import pion.tech.pionbase.app.domain.model.RemoteConfigData
import pion.tech.pionbase.model.RemoteConfigDataModel

fun RemoteConfigData.toPresentation() =
    RemoteConfigDataModel(
        configShowAds = this.configShowAds,
        isRealData = this.isRealData,
        admobId = this.admobId,
    )