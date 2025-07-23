package pion.tech.pionbase.feature.home.domain.model

import android.graphics.drawable.Drawable

data class InstalledAppData(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String?,
    val isSystemApp: Boolean
)