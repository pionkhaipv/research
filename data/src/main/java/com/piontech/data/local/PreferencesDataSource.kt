package com.piontech.data.local

interface PreferencesDataSource {
    var isPremium: Boolean
    var token: String?
    var floatValue: Float
    var stringSet: Set<String>
}