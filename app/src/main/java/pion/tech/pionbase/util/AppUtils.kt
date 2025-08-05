package pion.tech.pionbase.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import pion.tech.pionbase.R

/**
 * Utility class for app-related operations
 */
object AppUtils {
    /**
     * Safely loads an app icon by package name
     * Returns default icon if there's an exception
     */
    fun loadAppIcon(context: Context, packageName: String): Drawable {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            ContextCompat.getDrawable(context, R.drawable.ic_app_scan_default)
                ?: throw IllegalStateException("Default icon resource not found")
        }
    }
}