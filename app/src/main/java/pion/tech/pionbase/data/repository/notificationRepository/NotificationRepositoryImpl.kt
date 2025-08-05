package pion.tech.pionbase.data.repository.notificationRepository

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionDtoModel
import pion.tech.pionbase.data.model.notification.NotificationDtoModel
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.service.PionNotificationListenerService
import pion.tech.pionbase.util.NotifyListenerPermissionManager
import pion.tech.pionbase.util.Result
import timber.log.Timber
import javax.inject.Inject

class NotificationRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val dataStoreRepository: DataStoreRepository,
    ) : NotificationRepository {
        override fun getRecentNotifications(): Flow<Result<List<NotificationDtoModel>>> =
            flow {
                try {
                    // Try to get recent notifications from the notification listener service
                    try {
                        // Check if we can access the recentNotifications flow
                        val recentNotifications =
                            PionNotificationListenerService.recentNotifications
                                .take(50) // Take last 50 notifications
                                .toList()
                                .reversed() // Show most recent first

                        emit(Result.Success(recentNotifications))
                    } catch (serviceException: Exception) {
                        // If we can't access the service, return an empty list
                        Timber.d("Could not access notification service, returning empty list: ${serviceException.message}")
                        emit(Result.Success(emptyList()))
                    }
                } catch (exception: Exception) {
                    Timber.e("Error getting recent notifications: $exception")
                    emit(Result.Error(exception))
                }
            }.flowOn(Dispatchers.IO)

        override fun getAppsWithNotificationPermissions(): Flow<Result<List<AppNotificationPermissionDtoModel>>> =
            flow {
                try {
                    val packageManager = context.packageManager
                    val installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

                    val filteredApps =
                        installedPackages
                            .filter { appInfo -> shouldIncludeApp(appInfo, packageManager) }

                    // Process each app and create the model
                    val apps =
                        filteredApps
                            .map { appInfo -> createAppPermissionModel(appInfo, packageManager) }
                            .sortedBy { it.appName.lowercase() }

                    emit(Result.Success(apps))
                } catch (exception: Exception) {
                    Timber.e("Error getting apps with notification permissions: $exception")
                    emit(Result.Error(exception))
                }
            }.flowOn(Dispatchers.IO)

        /**
         * Determines if an app should be included based on Android version and permission requirements
         */
        private fun shouldIncludeApp(
            appInfo: ApplicationInfo,
            packageManager: PackageManager,
        ): Boolean =
            when {
                // For Android < 11: Show all user apps
                Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
                    isUserApp(appInfo)
                }
                // For Android >= 13: Show apps that have POST_NOTIFICATION permission declared
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    isUserApp(appInfo) && hasPostNotificationPermissionDeclared(appInfo.packageName, packageManager)
                }
                // For Android 11-12: Show all user apps (fallback)
                else -> {
                    isUserApp(appInfo)
                }
            }

        /**
         * Checks if the app is a user-installed app (not system app)
         */
        private fun isUserApp(appInfo: ApplicationInfo): Boolean = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0

        /**
         * Checks if the app has POST_NOTIFICATIONS permission declared in its manifest
         */
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private fun hasPostNotificationPermissionDeclared(
            packageName: String,
            packageManager: PackageManager,
        ): Boolean =
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                val permissions = packageInfo.requestedPermissions
                permissions?.contains(Manifest.permission.POST_NOTIFICATIONS) == true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        /**
         * Checks if the app has notification permissions enabled at system level
         */
        private fun hasSystemNotificationPermission(): Boolean =
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
                e.printStackTrace()
                // Default to enabled if we can't determine the status
                true
            }

        /**
         * Safely gets the app name from PackageManager
         */
        private fun getAppName(
            appInfo: ApplicationInfo,
            packageManager: PackageManager,
        ): String =
            try {
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                appInfo.packageName
            }

        /**
         * Creates an AppNotificationPermissionDtoModel from ApplicationInfo
         */
        private suspend fun createAppPermissionModel(
            appInfo: ApplicationInfo,
            packageManager: PackageManager,
        ): AppNotificationPermissionDtoModel {
            // Get blocked packages from data store
            val blockedPackagesResult = dataStoreRepository.getBlockedPackages().first()
            val isBlocked =
                if (blockedPackagesResult is Result.Success) {
                    blockedPackagesResult.data.contains(appInfo.packageName)
                } else {
                    false // Default to not blocked if we can't get the data
                }

            val hasNotificationPermission = hasSystemNotificationPermission()

            // Load app icon
            val appIcon =
                try {
                    packageManager.getApplicationIcon(appInfo.packageName)
                } catch (e: Exception) {
                    Timber.e("Error loading app icon for ${appInfo.packageName}: ${e.message}")
                    null
                }

            return AppNotificationPermissionDtoModel(
                packageName = appInfo.packageName,
                appName = getAppName(appInfo, packageManager),
                isNotificationEnabled = !isBlocked && hasNotificationPermission,
                appIcon = appIcon,
            )
        }

        override fun toggleNotificationPermission(
            packageName: String,
            enabled: Boolean,
        ): Flow<Result<Boolean>> =
            flow {
                try {
                    // Get current blocked packages from data store
                    val blockedPackagesResult = dataStoreRepository.getBlockedPackages().first()

                    if (blockedPackagesResult is Result.Success) {
                        // Create a mutable copy of the blocked packages set
                        val blockedPackages = blockedPackagesResult.data.toMutableSet()

                        // Update the set based on the enabled parameter
                        if (enabled) {
                            // Enable notifications (remove from blocked packages)
                            blockedPackages.remove(packageName)
                            Timber.d("Removing $packageName from blocked packages")
                        } else {
                            // Disable notifications (add to blocked packages)
                            blockedPackages.add(packageName)
                            Timber.d("Adding $packageName to blocked packages")
                        }

                        // Save the updated set to the data store
                        dataStoreRepository.setBlockedPackages(blockedPackages)
                        Timber.d("Saved ${blockedPackages.size} blocked packages to data store")

                        // Always reload blocked packages in the service's companion object
                        // This ensures the service has the latest data even if it wasn't available during the update
                        PionNotificationListenerService.reloadBlockedPackages()
                        Timber.d("Triggered reload of blocked packages in service")

                        // Also update the service instance if it's available (but don't fail if it's not)
                        val service = PionNotificationListenerService.getInstance()
                        if (service != null) {
                            if (enabled) {
                                // Enable notifications (unblock the package)
                                service.unblockNotificationsFromPackage(packageName)
                            } else {
                                // Disable notifications (block the package)
                                service.blockNotificationsFromPackage(packageName)
                            }
                            Timber.d("Updated notification service for package: $packageName, enabled: $enabled")
                        } else {
                            Timber.d("Notification service instance not available, but data was reloaded in companion object")
                        }

                        // Return success regardless of service availability
                        emit(Result.Success(enabled))
                    } else if (blockedPackagesResult is Result.Error) {
                        // If we couldn't get the blocked packages, propagate the error
                        Timber.e("Error getting blocked packages: ${blockedPackagesResult.error}")
                        emit(Result.Error(blockedPackagesResult.error))
                    }
                } catch (exception: Exception) {
                    Timber.e("Error toggling notification permission: $exception")
                    emit(Result.Error(exception))
                }
            }.flowOn(Dispatchers.IO)

        override fun isNotificationListenerEnabled(): Flow<Result<Boolean>> =
            flow {
                try {
                    val hasSystemPermission = NotifyListenerPermissionManager.areAllNotificationPermissionsGranted(context)
                    // Check internal monitoring state from data store
                    val monitoringEnabledResult = dataStoreRepository.getNotificationMonitoringEnabled().first()
                    val isInternallyEnabled =
                        if (monitoringEnabledResult is Result.Success) {
                            monitoringEnabledResult.data
                        } else {
                            // Default to false if we can't get the data
                            Timber.e("Error getting monitoring state: ${(monitoringEnabledResult as? Result.Error)?.error}")
                            false
                        }

                    // Return true only if both system permission and internal monitoring are enabled
                    val isEnabled = hasSystemPermission && isInternallyEnabled
                    emit(Result.Success(isEnabled))
                } catch (exception: Exception) {
                    Timber.e("Error checking if notification listener is enabled: $exception")
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
                        // Save the monitoring state to data store
                        val result = dataStoreRepository.setNotificationMonitoringEnabled(enabled)

                        if (result is Result.Success) {
                            Timber.d("Saved notification monitoring state to data store: $enabled")

                            // Also try to update the service if it's available (but don't fail if it's not)
                            try {
                                val service = PionNotificationListenerService.getInstance()
                                if (service != null) {
                                    // Control internal monitoring state in the service
                                    PionNotificationListenerService.setMonitoringEnabled(enabled)
                                    Timber.d("Updated notification service monitoring state: $enabled")
                                } else {
                                    Timber.d("Notification service not available, but data store was updated")
                                }
                            } catch (serviceException: Exception) {
                                Timber.e("Error updating service monitoring state: $serviceException")
                                // Don't fail if we can't update the service
                            }

                            // Return the actual state: enabled only if both system permission and internal state are true
                            val actualState = hasSystemPermission && enabled
                            emit(Result.Success(actualState))
                        } else if (result is Result.Error) {
                            // If we couldn't save to data store, propagate the error
                            Timber.e("Error saving notification monitoring state: ${result.error}")
                            emit(Result.Error(result.error))
                        }
                    }
                } catch (exception: Exception) {
                    Timber.e("Error setting notification listener enabled: $exception")
                    emit(Result.Error(exception))
                }
            }.flowOn(Dispatchers.IO)
    }
