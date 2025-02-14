package com.piontech.domain.usecase

import com.piontech.domain.repository.PreferencesRepository

class PreferencesUseCase (
    private val preferencesRepository: PreferencesRepository
) {

    fun getPremiumStatus(): Boolean {
        return preferencesRepository.isPremium
    }

    fun setPremiumStatus(isPremium: Boolean) {
        preferencesRepository.isPremium = isPremium
    }

    fun getToken(): String? {
        return preferencesRepository.token
    }

    fun setToken(token: String?) {
        preferencesRepository.token = token
    }

    fun getFloatValue(): Float {
        return preferencesRepository.floatValue
    }

    fun setFloatValue(value: Float) {
        preferencesRepository.floatValue = value
    }

    fun getStringSet(): Set<String> {
        return preferencesRepository.stringSet
    }

    fun setStringSet(values: Set<String>) {
        preferencesRepository.stringSet = values
    }
}
