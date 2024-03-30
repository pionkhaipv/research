package pion.tech.pionbase.util

import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefUtil {
    companion object{

        lateinit var sharedPreferences: SharedPreferences
        lateinit var editor: SharedPreferences.Editor

        var isPremium: Boolean
            get() = sharedPreferences.getBoolean("isPremium", false)
            set(value) {
                editor.putBoolean("isPremium", value).commit()
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

}