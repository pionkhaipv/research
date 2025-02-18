package com.piontech.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.piontech.data.model.RemoteConfigDataEntity
import com.piontech.data.model.toDomain
import com.piontech.domain.model.RemoteConfigData
import com.piontech.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class RemoteConfigRepositoryImpl(private val remoteConfig: FirebaseRemoteConfig) :
    RemoteConfigRepository {

    override suspend fun fetchRemoteConfig(): Flow<RemoteConfigData> {
        return flow {
            val data = withTimeoutOrNull(7000) { fetchRemoteConfigData() } ?: getDefaultRemoteConfigData()
            emit(data)
        }
    }

    private suspend fun fetchRemoteConfigData(): RemoteConfigData {
        return suspendCancellableCoroutine { cont ->
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                val configJson = remoteConfig.getString("config_show_ads")
                val admobId = remoteConfig.getString("admob_id")
                val result = RemoteConfigDataEntity(
                    configShowAds = configJson,
                    isRealData = task.isSuccessful,
                    admobId = admobId
                ).toDomain()
                cont.resume(result)
            }
        }
    }

    private fun getDefaultRemoteConfigData(): RemoteConfigData {
        val configJson = remoteConfig.getString("config_show_ads") // Lấy từ default
        val admobId = remoteConfig.getString("admob_id")
        return RemoteConfigDataEntity(
            configShowAds = configJson,
            isRealData = false,
            admobId = admobId
        ).toDomain()
    }

}
