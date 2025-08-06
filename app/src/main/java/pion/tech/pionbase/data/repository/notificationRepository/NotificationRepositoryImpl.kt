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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import pion.tech.pionbase.data.local.dao.NotificationDAO
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionDtoModel
import pion.tech.pionbase.data.model.notification.NotificationEntity
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.service.PionNotificationListenerService
import pion.tech.pionbase.util.NotifyListenerManager
import pion.tech.pionbase.util.Result
import timber.log.Timber
import javax.inject.Inject

class NotificationRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val dataStoreRepository: DataStoreRepository,
        private val notificationDAO: NotificationDAO,
    ) : NotificationRepository {
        override fun getRecentNotifications(): Flow<Result<List<NotificationEntity>>> =
            flow<Result<List<NotificationEntity>>> {
                // Get recent notifications from Room database
                notificationDAO.getRecentNotifications(50).collect { entities ->
                    // Return entities directly without conversion
                    emit(Result.Success(entities))
                }
            }.catch { exception ->
                Timber.e("Error getting recent notifications from database: $exception")
                emit(Result.Error(exception))
            }.flowOn(Dispatchers.IO)

        override fun getAppsWithNotificationPermissions(): Flow<Result<List<AppNotificationPermissionDtoModel>>> =
            flow<Result<List<AppNotificationPermissionDtoModel>>> {
                val packageManager = context.packageManager
                val installedPackages =
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

                val filteredApps =
                    installedPackages
                        .filter { appInfo -> shouldIncludeApp(appInfo, packageManager) }

                // Process each app and create the model
                val apps =
                    filteredApps
                        .map { appInfo -> createAppPermissionModel(appInfo, packageManager) }
                        .sortedBy { it.appName.lowercase() }

                emit(Result.Success(apps))
            }.catch { exception ->
                Timber.e("Error getting apps with notification permissions: $exception")
                emit(Result.Error(exception))
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
                    isUserApp(appInfo) &&
                        hasPostNotificationPermissionDeclared(
                            appInfo.packageName,
                            packageManager,
                        )
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
                val packageInfo =
                    packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
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
                false
            }

        /**
         * Gets the app name from the package info
         */
        private fun getAppName(
            appInfo: ApplicationInfo,
            packageManager: PackageManager,
        ): String =
            try {
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                // If we can't get the app name, use the package name as fallback
                appInfo.packageName
            }

        /**
         * Creates an AppNotificationPermissionDtoModel from ApplicationInfo
         */
        private fun createAppPermissionModel(
            appInfo: ApplicationInfo,
            packageManager: PackageManager,
        ): AppNotificationPermissionDtoModel {
            val packageName = appInfo.packageName
            val appName = getAppName(appInfo, packageManager)

            // Check if this package is in the blocked list
            val isBlocked = PionNotificationListenerService.isPackageBlocked(packageName)

            // Get app icon
            val icon =
                try {
                    packageManager.getApplicationIcon(packageName)
                } catch (e: Exception) {
                    null
                }

            // For Android 13+, we should check if the app has notification permission granted
            // For simplicity, we'll just check if it's in our blocked list
            val hasPermission = !isBlocked

            return AppNotificationPermissionDtoModel(
                packageName = packageName,
                appName = appName,
                hasNotificationPermission = hasPermission,
                icon = icon,
            )
        }

        override fun toggleNotificationPermission(
            packageName: String,
            enabled: Boolean,
        ): Flow<Result<Boolean>> =
            flow<Result<Boolean>> {
                // Get current blocked packages
                val blockedPackagesResult = dataStoreRepository.getBlockedPackages().first()

                if (blockedPackagesResult is Result.Success) {
                    val currentBlockedPackages = blockedPackagesResult.data.toMutableSet()

                    if (enabled) {
                        // Remove from blocked packages if enabling
                        currentBlockedPackages.remove(packageName)
                    } else {
                        // Add to blocked packages if disabling
                        currentBlockedPackages.add(packageName)
                    }

                    // Save updated blocked packages
                    dataStoreRepository.setBlockedPackages(currentBlockedPackages)

                    // Reload blocked packages in the notification service
                    PionNotificationListenerService.reloadBlockedPackages()

                    // If we have an active service instance, update it directly
                    val service = PionNotificationListenerService.getInstance()
                    if (service != null) {
                        if (enabled) {
                            service.unblockNotificationsFromPackage(packageName)
                            Timber.d("Unblocked notifications from $packageName")
                        } else {
                            service.blockNotificationsFromPackage(packageName)
                            Timber.d("Blocked notifications from $packageName")
                        }
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
            }.catch { exception ->
                Timber.e("Error toggling notification permission: $exception")
                emit(Result.Error(exception))
            }.flowOn(Dispatchers.IO)

        override fun isNotificationListenerEnabled(): Flow<Result<Boolean>> =
            flow<Result<Boolean>> {
                val hasSystemPermission = NotifyListenerManager.isGrandNotifyListenerPermission(context)
                // Check internal monitoring state from data store
                val monitoringEnabledResult =
                    dataStoreRepository.getNotificationMonitoringEnabled().first()
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
            }.catch { exception ->
                Timber.e("Error checking if notification listener is enabled: $exception")
                emit(Result.Error(exception))
            }.flowOn(Dispatchers.IO)

        override fun setNotificationListenerEnabled(enabled: Boolean): Flow<Result<Boolean>> =
            flow<Result<Boolean>> {
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
            }.catch { exception ->
                Timber.e("Error setting notification listener enabled: $exception")
                emit(Result.Error(exception))
            }.flowOn(Dispatchers.IO)

        override fun saveNotification(notification: NotificationEntity): Flow<Result<NotificationEntity>> =
            flow<Result<NotificationEntity>> {
                val savedEntity = notificationDAO.insertOrUpdateNotification(notification)
                Timber.d("Saved notification from ${notification.packageName} to database")
                emit(Result.Success(savedEntity))
            }.catch { exception ->
                Timber.e("Error saving notification to database: $exception")
                emit(Result.Error(exception))
            }.flowOn(Dispatchers.IO)

        override fun sendTestNotification(): Flow<Result<Boolean>> =
            flow<Result<Boolean>> {
                // Check if notification listener service is enabled
                val isEnabledResult = isNotificationListenerEnabled().first()

                if (isEnabledResult is Result.Error) {
                    emit(Result.Error(Exception("Failed to check if notification listener is enabled: ${isEnabledResult.error.message}")))
                    return@flow
                }

                val isEnabled = (isEnabledResult as Result.Success).data
                if (!isEnabled) {
                    emit(Result.Error(Exception("Notification listener service is not enabled. Please enable it first.")))
                    return@flow
                }

                // List of package names to randomly select from
                val packageNames =
                    listOf(
                        "co.ardrawing",
                        "co.cameradetector",
                        "co.piontech.flash.flashlight.flashalert.flashoncall",
                    )

                // Randomly select a package name
                val randomPackage = packageNames.random()
                Timber.d("Selected random package for test notification: $randomPackage")

                // Get app name for the selected package (or use package name if not found)
                val appName =
                    try {
                        val packageManager = context.packageManager
                        val appInfo = packageManager.getApplicationInfo(randomPackage, 0)
                        packageManager.getApplicationLabel(appInfo).toString()
                    } catch (e: Exception) {
                        // If package not found, just use the package name
                        Timber.d("Package not found, using package name as app name: $randomPackage")
                        randomPackage
                    }

                // Create a test notification
                val testNotification =
                    NotificationEntity(
                        packageName = randomPackage,
                        appName = appName,
                        title = "Test Notification from $randomPackage",
                        content = "This is a test notification to verify the service is working. Random package: $randomPackage. Time: ${System.currentTimeMillis()}",
                        timestamp = System.currentTimeMillis(),
                        notificationCount = 1,
                    )

                // Save the test notification - use first() instead of collect to avoid Flow transparency violation
                val saveResult = saveNotification(testNotification).first()

                when (saveResult) {
                    is Result.Success -> {
                        Timber.d("Test notification saved successfully")
                        emit(Result.Success(true))
                    }

                    is Result.Error -> {
                        Timber.e("Failed to save test notification: ${saveResult.error}")
                        emit(Result.Error(Exception("Failed to save test notification: ${saveResult.error.message}")))
                    }
                }
            }.catch { exception ->
                Timber.e("Error sending test notification: $exception")
                emit(Result.Error(exception))
            }.flowOn(Dispatchers.IO)
    }
