package pion.tech.pionbase.data.model.hiddenApp

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HiddenAppUIModel(
    val packageName: String,
    val appName: String,
    val versionName: String?,
    val isSystemApp: Boolean,
) : Parcelable {

    // Safe method to load icon when needed
    fun loadIcon(context: Context): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }
}

fun HiddenAppDtoModel.toPresentation(): HiddenAppUIModel {
    return HiddenAppUIModel(
        packageName = this.packageName,
        appName = this.appName,
        versionName = this.versionName,
        isSystemApp = this.isSystemApp,
    )
}
