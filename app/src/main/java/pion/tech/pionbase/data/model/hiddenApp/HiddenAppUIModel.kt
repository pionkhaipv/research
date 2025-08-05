package pion.tech.pionbase.data.model.hiddenApp

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class HiddenAppUIModel(
    val packageName: String,
    val appName: String,
    val versionName: String?,
    val isSystemApp: Boolean,
    val appSize: Long = 0L,
) : Parcelable

fun HiddenAppDtoModel.toPresentation(): HiddenAppUIModel =
    HiddenAppUIModel(
        packageName = this.packageName,
        appName = this.appName,
        versionName = this.versionName,
        isSystemApp = this.isSystemApp,
        appSize = this.appSize,
    )

/**
 * Format app size to human-readable string with decimal precision
 */
fun HiddenAppUIModel.formatAppSize(): String =
    when {
        appSize < 1024 -> "${appSize}B"
        appSize < 1024 * 1024 -> {
            // Convert to KB with 1 decimal place
            val kb = appSize / 1024.0
            String.format(Locale.US, "%.1f KB", kb)
        }

        appSize < 1024 * 1024 * 1024 -> {
            // Convert to MB with 2 decimal places
            val mb = appSize / (1024.0 * 1024.0)
            String.format(Locale.US, "%.2f MB", mb)
        }

        else -> {
            // Convert to GB with 2 decimal places
            val gb = appSize / (1024.0 * 1024.0 * 1024.0)
            String.format(Locale.US, "%.2f GB", gb)
        }
    }
