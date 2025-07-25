package pion.tech.pionbase.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import pion.tech.pionbase.service.PionNotificationListenerService

object NotificationPermissionManager {
    /**
     * Check if notification listener service is enabled for this app
     */
    fun isNotificationAccessGranted(context: Context): Boolean {
        val enabledListeners =
            Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners",
            )
        // Check for the full service component name
        val componentName = ComponentName(context, PionNotificationListenerService::class.java)
        val serviceComponentName = componentName.flattenToString()
        Log.d("asgagwwawga", "enabledListeners: $enabledListeners")
        Log.d("asgagwwawga", "serviceComponentName: $serviceComponentName")
        Log.d("asgagwwawga", "contains: ${enabledListeners?.contains(serviceComponentName)}")
        return enabledListeners?.contains(serviceComponentName) == true
    }

    /**
     * Request notification access permission by opening system settings
     */
    fun requestNotificationAccess(fragment: Fragment) {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            fragment.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general settings if specific settings not available
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            fragment.startActivity(intent)
        }
    }

    /**
     * Check all required permissions for notification functionality
     */
    fun areAllNotificationPermissionsGranted(context: Context): Boolean = isNotificationAccessGranted(context)

    /**
     * Get list of missing permissions for notification functionality
     */
    fun getMissingNotificationPermissions(context: Context): List<String> {
        val missingPermissions = mutableListOf<String>()

        if (!isNotificationAccessGranted(context)) {
            missingPermissions.add("Notification Access")
        }

        return missingPermissions
    }
}
