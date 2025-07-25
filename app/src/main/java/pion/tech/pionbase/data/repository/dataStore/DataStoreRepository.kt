package pion.tech.pionbase.data.repository.dataStore

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.util.Result

interface DataStoreRepository {
    fun getIsPremium(): Flow<Result<Boolean>>

    suspend fun setIsPremium(isPremium: Boolean): Result<Unit>

    fun getToken(): Flow<Result<String?>>

    suspend fun setToken(token: String): Result<Unit>

    // Notification Manager methods
    fun getBlockedPackages(): Flow<Result<Set<String>>>

    suspend fun setBlockedPackages(packages: Set<String>): Result<Unit>

    fun getNotificationMonitoringEnabled(): Flow<Result<Boolean>>

    suspend fun setNotificationMonitoringEnabled(enabled: Boolean): Result<Unit>
}
