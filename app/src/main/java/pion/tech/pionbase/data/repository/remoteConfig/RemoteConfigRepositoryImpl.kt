package pion.tech.pionbase.data.repository.remoteConfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import pion.tech.pionbase.data.model.remoteConfig.RemoteConfigDtoModel
import pion.tech.pionbase.util.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "RemoteConfigRepositoryI"

@Singleton
class RemoteConfigRepositoryImpl
    @Inject
    constructor(
        private val remoteConfig: FirebaseRemoteConfig,
    ) : RemoteConfigRepository {
        companion object {
            private const val TIMEOUT_MS = 7000L

            // Remote config keys
            private const val KEY_CONFIG_SHOW_ADS = "config_show_ads"
            private const val KEY_ADMOB_ID = "admob_id"
        }

        // Cache for remote config data
        private val _cachedRemoteConfig = MutableStateFlow<RemoteConfigDtoModel?>(null)

        override fun fetchRemoteConfig(): Flow<Result<RemoteConfigDtoModel>> =
            flow<Result<RemoteConfigDtoModel>> {
                try {
                    val data =
                        withTimeoutOrNull(TIMEOUT_MS) { fetchRemoteConfigData() }
                            ?: getDefaultRemoteConfigData()

                    // Update cache
                    _cachedRemoteConfig.value = data
                    emit(Result.Success(data))
                } catch (e: Exception) {
                    Timber.tag(TAG).d("fetchRemoteConfig error: $e")
                    val defaultData = getDefaultRemoteConfigData()
                    _cachedRemoteConfig.value = defaultData
                    emit(Result.Success(defaultData))
                }
            }.catch { e ->
                Timber.tag(TAG).d("fetchRemoteConfig flow error: $e")
                val defaultData = getDefaultRemoteConfigData()
                _cachedRemoteConfig.value = defaultData
                emit(Result.Success(defaultData))
            }.flowOn(Dispatchers.IO)

        override fun getCachedRemoteConfig(): Flow<RemoteConfigDtoModel?> = _cachedRemoteConfig.asStateFlow()

        private suspend fun fetchRemoteConfigData(): RemoteConfigDtoModel =
            suspendCancellableCoroutine { cont ->
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

        private fun getDefaultRemoteConfigData(): RemoteConfigDtoModel = createRemoteConfigEntity(isRealData = false)

        private fun createRemoteConfigEntity(isRealData: Boolean): RemoteConfigDtoModel =
            RemoteConfigDtoModel(
                firebaseRemoteConfig = remoteConfig,
                isRealData = isRealData,
            )

        private fun getStringValue(
            key: String,
            defaultValue: String = "",
        ): String =
            runCatching {
                remoteConfig.getString(key)
            }.getOrDefault(defaultValue)

        private fun getLongValue(
            key: String,
            defaultValue: Long = 0L,
        ): Long = runCatching { remoteConfig.getLong(key) }.getOrDefault(defaultValue)
    }
