package pion.tech.pionbase.main.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.Dispatchers
import pion.tech.pionbase.main.domain.model.RemoteConfigData
import pion.tech.pionbase.main.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import kotlin.coroutines.resume

private const val TAG = "RemoteConfigRepositoryI"

class RemoteConfigRepositoryImpl(private val remoteConfig: FirebaseRemoteConfig) :
    RemoteConfigRepository {

    companion object {
        private const val TIMEOUT_MS = 7000L

        // Remote config keys
        private const val KEY_CONFIG_SHOW_ADS = "config_show_ads"
        private const val KEY_ADMOB_ID = "admob_id"
    }

    override suspend fun fetchRemoteConfig(): Flow<RemoteConfigData> {
        return flow<RemoteConfigData> {
            val data = withTimeoutOrNull(TIMEOUT_MS) { fetchRemoteConfigData() }
                ?: getDefaultRemoteConfigData()
            emit(data)
        }.catch { e ->
            Timber.tag(TAG).d("fetchRemoteConfig: $e")
            emit(getDefaultRemoteConfigData())
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun fetchRemoteConfigData(): RemoteConfigData {
        return suspendCancellableCoroutine { cont ->
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                try {
                    val result = createRemoteConfigEntity(task.isSuccessful)
                    cont.resume(result)
                } catch (e: Exception) {
                    e.printStackTrace()
                    cont.resume(getDefaultRemoteConfigData())
                }
            }
        }
    }

    private fun getDefaultRemoteConfigData(): RemoteConfigData {
        return createRemoteConfigEntity(isRealData = false)
    }

    private fun createRemoteConfigEntity(isRealData: Boolean): RemoteConfigData {
        return RemoteConfigData(
            configShowAds = getStringValue(KEY_CONFIG_SHOW_ADS),
            isRealData = isRealData,
            admobId = getStringValue(KEY_ADMOB_ID)
        )
    }

    private fun getStringValue(key: String, defaultValue: String = ""): String {
        return runCatching { remoteConfig.getString(key) }.getOrDefault(defaultValue)
    }

    private fun getLongValue(key: String, defaultValue: Long = 0L): Long {
        return runCatching { remoteConfig.getLong(key) }.getOrDefault(defaultValue)
    }

}
