package pion.tech.pionbase.app.domain.repository

import pion.tech.pionbase.app.domain.model.RemoteConfigData
import kotlinx.coroutines.flow.Flow

interface RemoteConfigRepository {
    suspend fun fetchRemoteConfig(): Flow<RemoteConfigData>
}