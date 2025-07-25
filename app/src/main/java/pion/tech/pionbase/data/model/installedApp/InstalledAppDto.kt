package pion.tech.pionbase.data.model.installedApp

import android.graphics.drawable.Drawable

data class InstalledAppDto(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String?,
    val isSystemApp: Boolean,
)
