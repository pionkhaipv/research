package com.piontech.data.repository

import com.piontech.data.local.sharePreference.PreferencesDataSource
import com.piontech.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

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