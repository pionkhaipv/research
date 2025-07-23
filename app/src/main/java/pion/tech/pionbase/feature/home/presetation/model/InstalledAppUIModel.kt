package pion.tech.pionbase.feature.home.presetation.model

import android.graphics.drawable.Drawable
import pion.tech.pionbase.feature.home.domain.model.InstalledAppData

data class InstalledAppUIModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String?,
    val isSystemApp: Boolean
)

fun InstalledAppData.toPresentation(): InstalledAppUIModel =
    InstalledAppUIModel(
        packageName = this.packageName,
        appName = this.appName,
        icon = this.icon,
        versionName = this.versionName,
        isSystemApp = this.isSystemApp
    )
