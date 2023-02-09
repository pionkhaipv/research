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

    var token: String?
        get() = sharedPreferences.getString("CachedToken", null)
        set(value) {
            editor.putString("CachedToken", value).commit()
        }

}