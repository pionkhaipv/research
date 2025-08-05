package pion.tech.pionbase.data.repository.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.util.Result
import java.io.IOException

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : DataStoreRepository {
    private val isPremiumKey = booleanPreferencesKey("isPremiumKey")
    private val tokenKey = stringPreferencesKey("tokenKey")
    private val blockedPackagesKey = stringSetPreferencesKey("blockedPackagesKey")
    private val notificationMonitoringEnabledKey = booleanPreferencesKey("notificationMonitoringEnabledKey")

    override fun getIsPremium(): Flow<Result<Boolean>> =
        dataStore.data
            .map { prefs ->
                Result.Success(prefs[isPremiumKey] ?: false) as Result<Boolean>
            }.catch { exception ->
                emit(Result.Error<Boolean>(exception) as Result<Boolean>)
            }

    override suspend fun setIsPremium(isPremium: Boolean): Result<Unit> =
        try {
            dataStore.edit {
                it[isPremiumKey] = isPremium
            }
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }

    override fun getToken(): Flow<Result<String?>> =
        dataStore.data
            .map { prefs ->
                Result.Success(prefs[tokenKey]) as Result<String?>
            }.catch { exception ->
                emit(Result.Error<String?>(exception) as Result<String?>)
            }

    override suspend fun setToken(token: String): Result<Unit> =
        try {
            dataStore.edit {
                it[tokenKey] = token
            }
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }

    override fun getBlockedPackages(): Flow<Result<Set<String>>> =
        dataStore.data
            .map { prefs ->
                Result.Success(prefs[blockedPackagesKey] ?: emptySet()) as Result<Set<String>>
            }.catch { exception ->
                emit(Result.Error<Set<String>>(exception) as Result<Set<String>>)
            }

    override suspend fun setBlockedPackages(packages: Set<String>): Result<Unit> =
        try {
            dataStore.edit {
                it[blockedPackagesKey] = packages
            }
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }

    override fun getNotificationMonitoringEnabled(): Flow<Result<Boolean>> =
        dataStore.data
            .map { prefs ->
                Result.Success(prefs[notificationMonitoringEnabledKey] ?: false) as Result<Boolean>
            }.catch { exception ->
                emit(Result.Error<Boolean>(exception) as Result<Boolean>)
            }

    override suspend fun setNotificationMonitoringEnabled(enabled: Boolean): Result<Unit> =
        try {
            dataStore.edit {
                it[notificationMonitoringEnabledKey] = enabled
            }
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
}
