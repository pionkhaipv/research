package pion.tech.pionbase.data.model.hiddenApp

import android.graphics.drawable.Drawable

data class HiddenAppDtoModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String?,
    val isSystemApp: Boolean,
    val appSize: Long = 0L,
)