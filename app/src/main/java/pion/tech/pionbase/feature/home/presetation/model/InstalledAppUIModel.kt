package pion.tech.pionbase.feature.home.presetation.model

import android.graphics.drawable.Drawable

data class InstalledAppUIModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String?,
    val isSystemApp: Boolean
)