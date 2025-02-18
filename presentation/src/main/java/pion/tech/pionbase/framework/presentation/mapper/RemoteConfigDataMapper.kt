package pion.tech.pionbase.framework.presentation.mapper

import com.piontech.domain.model.RemoteConfigData
import pion.tech.pionbase.framework.presentation.model.RemoteConfigDataModel

fun RemoteConfigData.toPresentation() =
    RemoteConfigDataModel(
        configShowAds = this.configShowAds,
        isRealData = this.isRealData,
        admobId = this.admobId,
    )