package pion.tech.pionbase.app.domain.usecase

import pion.tech.pionbase.app.domain.model.RemoteConfigData
import pion.tech.pionbase.app.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow

class FetchRemoteConfigUseCase(private val repository: RemoteConfigRepository) {
    suspend operator fun invoke(): Flow<RemoteConfigData> {
        return repository.fetchRemoteConfig()
    }
}