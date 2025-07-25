package pion.tech.pionbase.data.repository.remoteConfig

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.app.data.model.RemoteConfigDto

interface RemoteConfigRepository {
    suspend fun fetchRemoteConfig(): Flow<RemoteConfigDto>
}
