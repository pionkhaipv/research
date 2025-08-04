package pion.tech.pionbase.data.model.concernApp

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConcernAppUIModel(
    val packageName: String,
    val appName: String,
    val versionName: String?,
    val isSystemApp: Boolean,
    val permissions: List<String>,
    val dangerLevel: DangerLevel,
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

fun ConcernAppDtoModel.toPresentation(): ConcernAppUIModel {
    return ConcernAppUIModel(
        packageName = this.packageName,
        appName = this.appName,
        versionName = this.versionName,
        isSystemApp = this.isSystemApp,
        permissions = this.permissions,
        dangerLevel = this.dangerLevel,
    )
}