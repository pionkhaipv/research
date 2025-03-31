package pion.tech.pionbase.app.data.repository

import pion.tech.pionbase.app.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.app.data.dataStore.PreferencesDataSource

class DataStoreRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource
) : DataStoreRepository {
    override suspend fun getIsPremium(): Flow<Boolean> {
        return preferencesDataSource.getIsPremium()
    }

    override suspend fun setIsPremium(isPremium: Boolean) {
        preferencesDataSource.setIsPremium(isPremium)
    }

    override suspend fun getToken(): Flow<String?> {
        return preferencesDataSource.getToken()
    }

    override suspend fun setToken(token: String) {
        preferencesDataSource.setToken(token)
    }

}