package com.piontech.data.model

import com.piontech.domain.model.RemoteConfigData

data class RemoteConfigDataEntity(
    val configShowAds: String,
    val isRealData: Boolean
)

fun RemoteConfigDataEntity.toDomain() =
    RemoteConfigData(configShowAds = this.configShowAds, isRealData = this.isRealData)
