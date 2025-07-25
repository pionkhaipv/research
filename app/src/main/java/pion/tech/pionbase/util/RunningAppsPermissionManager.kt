package pion.tech.pionbase.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.Fragment

object RunningAppsPermissionManager {
    
    /**
     * Check if all required permissions for running apps functionality are granted
     */
    fun areAllRunningAppsPermissionsGranted(context: Context): Boolean {
        return hasQueryAllPackagesPermission(context)
    }
    
    /**
     * Check if QUERY_ALL_PACKAGES permission is effectively available
     * On Android 11+, this permission must be declared in manifest
     * On older versions, it's not needed
     */
    private fun hasQueryAllPackagesPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+, check if we can query installed applications
            try {
                val packageManager = context.packageManager
                val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                // If we can get a reasonable number of apps, permission is working
                installedApps.size > 10 // Arbitrary threshold to detect if permission is working
            } catch (e: Exception) {
                false
            }
        } else {
            // For older Android versions, no special permission needed
            true
        }
    }
    
    /**
     * Request running apps permissions by opening app settings
     * Since QUERY_ALL_PACKAGES is install-time permission, we direct user to app info
     */
    fun requestRunningAppsPermissions(fragment: Fragment) {
        try {
            // Open app settings where user can see permissions
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", fragment.requireContext().packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            fragment.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            fragment.startActivity(intent)
        }
    }
    
    /**
     * Get list of missing permissions for running apps functionality
     */
    fun getMissingRunningAppsPermissions(context: Context): List<String> {
        val missingPermissions = mutableListOf<String>()
        
        if (!hasQueryAllPackagesPermission(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                missingPermissions.add("Query All Packages (for app listing)")
            }
        }
        
        return missingPermissions
    }
    
    /**
     * Get user-friendly explanation for running apps permissions
     */
    fun getPermissionExplanation(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            "Running Apps Manager needs permission to access the list of installed applications to show which apps are currently running."
        } else {
            "Running Apps Manager functionality is available on your device."
        }
    }
}