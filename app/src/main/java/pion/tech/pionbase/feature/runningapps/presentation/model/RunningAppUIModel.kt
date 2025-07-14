package pion.tech.pionbase.feature.runningapps.presentation.model

import android.graphics.drawable.Drawable

data class RunningAppUIModel(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?,
    val isSystemApp: Boolean,
    val memoryUsage: Long = 0L, // in bytes
    val backgroundType: BackgroundType = BackgroundType.NONE,
)

enum class BackgroundType(
    val priority: Int,
    val description: String,
) {
    FOREGROUND_SERVICE(4, "Foreground Service"),
    BACKGROUND_SERVICE(3, "Background Service"),
    ACTIVE_PROCESS(2, "Active Process"),
    RECENT_USAGE(1, "Recent Usage"),
    NONE(0, "Not Running"),
}
