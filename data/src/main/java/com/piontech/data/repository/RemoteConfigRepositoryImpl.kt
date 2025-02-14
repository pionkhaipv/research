package com.piontech.data.repository

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.piontech.data.model.RemoteConfigDataEntity
import com.piontech.data.model.toDomain
import com.piontech.domain.model.RemoteConfigData
import com.piontech.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class RemoteConfigRepositoryImpl(private val remoteConfig: FirebaseRemoteConfig) :
    RemoteConfigRepository {

    override suspend fun fetchRemoteConfig(): RemoteConfigData {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(7000) {
                    val isSuccess = remoteConfig.fetchAndActivate().await()
                    val configJson = remoteConfig.getString("config_show_ads")
                    if (isSuccess) {
                        Log.d("sagagwgwagwagwa", "chay vao day2")
                        RemoteConfigDataEntity(
                            configShowAds = configJson,
                            isRealData = true
                        ).toDomain()
                    } else {
                        Log.d("sagagwgwagwagwa", "chay vao day")
                        RemoteConfigDataEntity(
                            configShowAds = configJson,
                            isRealData = false
                        ).toDomain()
                    }
                }
            } catch (e: Exception) {
                Log.d("sagagwgwagwagwa", "fetchRemoteConfig: $e")
                val configJson = remoteConfig.getString("config_show_ads")
                RemoteConfigDataEntity(configShowAds = configJson, isRealData = false).toDomain()
            }
        }
    }

}
