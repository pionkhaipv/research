package com.piontech.domain.usecase

import com.piontech.domain.model.RemoteConfigData
import com.piontech.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchRemoteConfigUseCase(private val repository: RemoteConfigRepository) {
    suspend operator fun invoke(): Flow<RemoteConfigData> {
        return repository.fetchRemoteConfig()
    }
}
