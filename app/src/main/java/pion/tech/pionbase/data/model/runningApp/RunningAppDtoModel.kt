package pion.tech.pionbase.data.model.runningApp

import android.graphics.drawable.Drawable

data class RunningAppDtoModel(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?,
    val isSystemApp: Boolean,
    val memoryUsage: Long = 0L, // in bytes
    val backgroundType: BackgroundType = BackgroundType.NONE,
)

fun RunningAppDtoModel.toPresentation(): RunningAppUIModel =
    RunningAppUIModel(
        packageName = this.packageName,
        appName = this.appName,
        appIcon = this.appIcon,
        isSystemApp = this.isSystemApp,
        memoryUsage = this.memoryUsage,
        backgroundType = this.backgroundType,
    )
