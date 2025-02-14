package com.piontech.data.repository

import com.piontech.data.local.PreferencesDataSource
import com.piontech.domain.repository.PreferencesRepository

class PreferencesRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource
) : PreferencesRepository {


    override var isPremium: Boolean
        get() = preferencesDataSource.isPremium
        set(value) {
            preferencesDataSource.isPremium = value
        }

    override var token: String?
        get() = preferencesDataSource.token
        set(value) {
            preferencesDataSource.token = value
        }
    override var floatValue: Float
        get() = preferencesDataSource.floatValue
        set(value) {
            preferencesDataSource.floatValue = value
        }
    override var stringSet: Set<String>
        get() = preferencesDataSource.stringSet
        set(value) {
            preferencesDataSource.stringSet = value
        }
}