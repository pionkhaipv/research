package pion.tech.pionbase.util

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefUtil
@Inject
constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) {

    var IS_PREMIUM: Boolean
        get() = sharedPreferences.getBoolean("IS_PREMIUM", false)
        set(value) {
            editor.putBoolean("IS_PREMIUM", value).commit()
        }

    var IS_F0: Boolean
        get() = sharedPreferences.getBoolean("IS_F0", false)
        set(value) {
            editor.putBoolean("IS_F0", value).commit()
        }


    var token: String?
        get() = sharedPreferences.getString("CachedToken", null)
        set(value) {
            editor.putString("CachedToken", value).commit()
        }

}