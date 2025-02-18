package pion.tech.pionbase.mapper

import com.piontech.domain.model.RemoteConfigData
import pion.tech.pionbase.model.RemoteConfigDataModel

fun RemoteConfigData.toPresentation() =
    RemoteConfigDataModel(
        configShowAds = this.configShowAds,
        isRealData = this.isRealData,
        admobId = this.admobId,
    )