package com.piontech.domain.repository

interface PreferencesRepository {
    var isPremium: Boolean
    var token: String?
    var floatValue: Float
    var stringSet: Set<String>
}
