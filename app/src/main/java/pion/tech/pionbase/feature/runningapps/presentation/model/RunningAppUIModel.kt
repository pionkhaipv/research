package pion.tech.pionbase.feature.runningapps.presentation.model

import android.graphics.drawable.Drawable

data class RunningAppUIModel(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?,
    val isSystemApp: Boolean,
    val memoryUsage: Long = 0L, // in bytes
)
