package com.piontech.domain.repository

import com.piontech.domain.model.RemoteConfigData
import kotlinx.coroutines.flow.Flow

interface RemoteConfigRepository {
    suspend fun fetchRemoteConfig(): Flow<RemoteConfigData>
}