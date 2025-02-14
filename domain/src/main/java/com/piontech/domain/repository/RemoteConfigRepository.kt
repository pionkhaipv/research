package com.piontech.domain.repository

import com.piontech.domain.model.RemoteConfigData

interface RemoteConfigRepository {
    suspend fun fetchRemoteConfig(): RemoteConfigData
}