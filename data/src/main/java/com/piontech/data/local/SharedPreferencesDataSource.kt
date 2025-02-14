package com.piontech.data.local

import android.content.SharedPreferences

class SharedPreferencesDataSource(
    private val sharedPreferences: SharedPreferences
) : PreferencesDataSource {

    override var isPremium: Boolean
        get() = sharedPreferences.getBoolean("isPremium", false)
        set(value) = sharedPreferences.edit().putBoolean("isPremium", value).apply()

    override var token: String?
        get() = sharedPreferences.getString("token", null)
        set(value) = sharedPreferences.edit().putString("token", value).apply()

    override var floatValue: Float
        get() = sharedPreferences.getFloat("floatValue", 0.0f)
        set(value) = sharedPreferences.edit().putFloat("floatValue", value).apply()

    override var stringSet: Set<String>
        get() = sharedPreferences.getStringSet("stringSet", emptySet()) ?: emptySet()
        set(value) = sharedPreferences.edit().putStringSet("stringSet", value).apply()
}
