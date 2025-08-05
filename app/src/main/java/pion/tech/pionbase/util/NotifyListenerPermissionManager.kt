package pion.tech.pionbase.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.fragment.app.Fragment
import pion.tech.pionbase.service.PionNotificationListenerService

object NotifyListenerPermissionManager {
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
    fun areAllNotificationPermissionsGranted(context: Context): Boolean {
        val enabledListeners =
            Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners",
            )
        // Check for the full service component name
        val componentName = ComponentName(context, PionNotificationListenerService::class.java)
        val serviceComponentName = componentName.flattenToString()
        return enabledListeners?.contains(serviceComponentName) == true
    }
}
