package pion.tech.pionbase.data.repository.dataStore

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    fun getIsPremium(): Flow<Boolean>

    suspend fun setIsPremium(isPremium: Boolean)

    fun getToken(): Flow<String?>

    suspend fun setToken(token: String)
}
