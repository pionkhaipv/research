package pion.tech.pionbase.data.repository.remoteConfig

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.remoteConfig.RemoteConfigDtoModel
import pion.tech.pionbase.util.Result

interface RemoteConfigRepository {
    fun fetchRemoteConfig(): Flow<Result<RemoteConfigDtoModel>>

    fun getCachedRemoteConfig(): Flow<RemoteConfigDtoModel?>
}
