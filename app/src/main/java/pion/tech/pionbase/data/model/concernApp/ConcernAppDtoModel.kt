package pion.tech.pionbase.data.model.concernApp

import android.graphics.drawable.Drawable

data class ConcernAppDtoModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String?,
    val isSystemApp: Boolean,
    val permissions: List<String>,
    val dangerLevel: DangerLevel,
)

enum class DangerLevel {
    SAFE,     // Green - no dangerous permissions
    MEDIUM,   // Yellow - some concerning permissions
    DANGEROUS // Red - high-risk permissions
}