package com.piontech.domain.usecase

import com.piontech.domain.model.RemoteConfigData
import com.piontech.domain.repository.RemoteConfigRepository

class FetchRemoteConfigUseCase(private val repository: RemoteConfigRepository) {
    suspend operator fun invoke(): RemoteConfigData {
        return repository.fetchRemoteConfig()
    }
}
