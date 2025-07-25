package pion.tech.pionbase.data.repository.notificationRepository

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionDtoModel
import pion.tech.pionbase.data.model.notification.NotificationDtoModel
import pion.tech.pionbase.service.PionNotificationListenerService
import pion.tech.pionbase.util.Result

class NotificationRepositoryImpl(
    @ApplicationContext private val context: Context,
) : NotificationRepository {
    override fun getRecentNotifications(): Flow<Result<List<NotificationDtoModel>>> =
        flow {
            try {
                // Get recent notifications from the notification listener service
                val recentNotifications =
                    PionNotificationListenerService.recentNotifications
                        .take(50) // Take last 50 notifications
                        .toList()
                        .reversed() // Show most recent first

                emit(Result.Success(recentNotifications))
            } catch (exception: Exception) {
                emit(Result.Error(exception))
            }
        }.flowOn(Dispatchers.IO)

    override fun getAppsWithNotificationPermissions(): Flow<Result<List<AppNotificationPermissionDtoModel>>> =
        flow {
            try {
                val packageManager = context.packageManager
                val installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

                val apps =
                    installedPackages
                        .filter { appInfo ->
                            // Filter based on Android version requirements
                            when {
                                // For Android < 11: Show all user apps
                                Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
                                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                                }
                                // For Android >= 13: Show apps that have POST_NOTIFICATION permission declared
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                                    val isUserApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                                    if (!isUserApp) return@filter false

                                    // Check if app has POST_NOTIFICATION permission declared in manifest
                                    try {
                                        val packageInfo =
                                            packageManager.getPackageInfo(
                                                appInfo.packageName,
                                                PackageManager.GET_PERMISSIONS,
                                            )
                                        val permissions = packageInfo.requestedPermissions
                                        permissions?.contains(Manifest.permission.POST_NOTIFICATIONS) == true
                                    } catch (e: Exception) {
                                        false
                                    }
                                }
                                // For Android 11-12: Show all user apps (fallback)
                                else -> {
                                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                                }
                            }
                        }.map { appInfo ->
                            // Check if this app is blocked by our service
                            val isBlocked = PionNotificationListenerService.isPackageBlocked(appInfo.packageName)

                            // Check if this specific app has notification permissions enabled
                            val hasNotificationPermission =
                                try {
                                    // For Android 13+, check if the app has POST_NOTIFICATION permission granted
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        // Check if the app has POST_NOTIFICATION permission granted (not just declared)
                                        val notificationManager = NotificationManagerCompat.from(context)
                                        notificationManager.areNotificationsEnabled() // This checks system-wide, we need per-app check

                                        // For now, assume all apps that declared the permission can potentially have it enabled
                                        // In a real implementation, we would need to check per-app notification settings
                                        true
                                    } else {
                                        // For older Android versions, assume notifications are enabled by default
                                        true
                                    }
                                } catch (e: Exception) {
                                    // Default to enabled if we can't determine the status
                                    true
                                }

                            AppNotificationPermissionDtoModel(
                                packageName = appInfo.packageName,
                                appName =
                                    try {
                                        packageManager.getApplicationLabel(appInfo).toString()
                                    } catch (e: Exception) {
                                        appInfo.packageName
                                    },
                                icon =
                                    try {
                                        packageManager.getApplicationIcon(appInfo)
                                    } catch (e: Exception) {
                                        null
                                    },
                                isNotificationEnabled = !isBlocked && hasNotificationPermission,
                            )
                        }.sortedBy { it.appName.lowercase() }

                emit(Result.Success(apps))
            } catch (exception: Exception) {
                emit(Result.Error(exception))
            }
        }.flowOn(Dispatchers.IO)

    override fun toggleNotificationPermission(
        packageName: String,
        enabled: Boolean,
    ): Flow<Result<Boolean>> =
        flow {
            try {
                val service = PionNotificationListenerService.getInstance()
                if (service != null) {
                    if (enabled) {
                        // Enable notifications (unblock the package)
                        service.unblockNotificationsFromPackage(packageName)
                    } else {
                        // Disable notifications (block the package)
                        service.blockNotificationsFromPackage(packageName)
                    }
                    emit(Result.Success(enabled))
                } else {
                    emit(Result.Error(Exception("Notification listener service not available")))
                }
            } catch (exception: Exception) {
                emit(Result.Error(exception))
            }
        }.flowOn(Dispatchers.IO)

    override fun isNotificationListenerEnabled(): Flow<Result<Boolean>> =
        flow {
            try {
                // Check system permission
                val enabledListeners =
                    Settings.Secure.getString(
                        context.contentResolver,
                        "enabled_notification_listeners",
                    )
                val hasSystemPermission = enabledListeners?.contains(context.packageName) == true

                // Check internal monitoring state
                val isInternallyEnabled = PionNotificationListenerService.isMonitoringEnabled()

                // Return true only if both system permission and internal monitoring are enabled
                val isEnabled = hasSystemPermission && isInternallyEnabled
                emit(Result.Success(isEnabled))
            } catch (exception: Exception) {
                emit(Result.Error(exception))
            }
        }.flowOn(Dispatchers.IO)

    override fun setNotificationListenerEnabled(enabled: Boolean): Flow<Result<Boolean>> =
        flow {
            try {
                // Check if notification listener permission is granted
                val enabledListeners =
                    Settings.Secure.getString(
                        context.contentResolver,
                        "enabled_notification_listeners",
                    )
                val hasSystemPermission = enabledListeners?.contains(context.packageName) == true

                if (enabled && !hasSystemPermission) {
                    // If user wants to enable but system permission is not granted, open settings
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    emit(Result.Success(false)) // Return false since permission is not granted yet
                } else {
                    // Control internal monitoring state
                    PionNotificationListenerService.setMonitoringEnabled(enabled)

                    // Return the actual state: enabled only if both system permission and internal state are true
                    val actualState = hasSystemPermission && enabled
                    emit(Result.Success(actualState))
                }
            } catch (exception: Exception) {
                emit(Result.Error(exception))
            }
        }.flowOn(Dispatchers.IO)
}
