package pion.tech.pionbase.service

import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pion.tech.pionbase.data.model.notification.NotificationEntity
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.data.repository.notificationRepository.NotificationRepository
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.onSuccess
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PionNotificationListenerService : NotificationListenerService() {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    companion object {
        private var instance: PionNotificationListenerService? = null
        private var dataStoreRepository: DataStoreRepository? = null
        private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private val _blockedPackages = mutableSetOf<String>()

        // Internal enabled state for notification monitoring
        private var _isMonitoringEnabled = true

        fun getInstance(): PionNotificationListenerService? = instance

        fun initializeStorage(dataStore: DataStoreRepository) {
            // Always store the reference and load data, even if already initialized
            dataStoreRepository = dataStore
            loadDataFromStorage()
        }

        /**
         * Reloads blocked packages from DataStore
         * This should be called whenever blocked packages are updated through the repository
         */
        fun reloadBlockedPackages() {
            serviceScope.launch {
                dataStoreRepository?.getBlockedPackages()?.first()?.onSuccess { packages ->
                    _blockedPackages.clear()
                    _blockedPackages.addAll(packages)
                    Timber.d("Reloaded ${packages.size} blocked packages from DataStore")
                }
            }
        }

        private fun loadDataFromStorage() {
            serviceScope.launch {
                // Load blocked packages
                dataStoreRepository?.getBlockedPackages()?.first()?.onSuccess { packages ->
                    _blockedPackages.clear()
                    _blockedPackages.addAll(packages)
                    Timber.d("Loaded ${packages.size} blocked packages from DataStore")
                }

                // Load monitoring state
                dataStoreRepository
                    ?.getNotificationMonitoringEnabled()
                    ?.first()
                    ?.onSuccess { enabled ->
                        _isMonitoringEnabled = enabled
                        Timber.d("Loaded monitoring state: $enabled from DataStore")
                    }
            }
        }

        private fun saveBlockedPackages() {
            serviceScope.launch {
                dataStoreRepository?.setBlockedPackages(_blockedPackages.toSet())?.onSuccess {
                    Timber.d("Saved ${_blockedPackages.size} blocked packages to DataStore")
                }
            }
        }

        private fun saveMonitoringState() {
            serviceScope.launch {
                dataStoreRepository
                    ?.setNotificationMonitoringEnabled(_isMonitoringEnabled)
                    ?.onSuccess {
                        Timber.d("Saved monitoring state: $_isMonitoringEnabled to DataStore")
                    }
            }
        }

        fun addBlockedPackage(packageName: String) {
            _blockedPackages.add(packageName)
            saveBlockedPackages()
        }

        fun removeBlockedPackage(packageName: String) {
            _blockedPackages.remove(packageName)
            saveBlockedPackages()
        }

        fun isPackageBlocked(packageName: String): Boolean = _blockedPackages.contains(packageName)

        fun getBlockedPackages(): Set<String> = _blockedPackages.toSet()

        fun setMonitoringEnabled(enabled: Boolean) {
            _isMonitoringEnabled = enabled
            saveMonitoringState()
        }

        fun isMonitoringEnabled(): Boolean = _isMonitoringEnabled
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Initialize persistent storage
        initializeStorage(dataStoreRepository)
        Timber.d("NotificationListenerService created")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Timber.d("NotificationListenerService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Timber.d("NotificationListenerService bound")
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        // Check if monitoring is enabled
        if (!_isMonitoringEnabled) {
            Timber.d("Notification monitoring is disabled, ignoring notification")
            return
        }

        sbn?.let { notification ->
            val packageName = notification.packageName

            // Check if this package is blocked
            if (_blockedPackages.contains(packageName)) {
                // Cancel the notification if it's from a blocked package
                cancelNotification(notification.key)
                Timber.d("Blocked notification from: $packageName")
                return
            }

            // Create notification model and emit to flow
            val notificationModel =
                NotificationEntity(
                    packageName = packageName,
                    appName = getAppName(packageName),
                    title = notification.notification.extras.getString("android.title"),
                    content = notification.notification.extras.getString("android.text"),
                    timestamp = notification.postTime,
                    notificationCount = 1
                )

            // Save notification to Room database
            serviceScope.launch {
                try {
                    notificationRepository.saveNotification(notificationModel).collect { result ->
                        when (result) {
                            is Result.Success -> {
                                Timber.d("Notification saved to database from: $packageName")
                            }
                            is Result.Error -> {
                                Timber.e("Error saving notification to database: ${result.error}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.e("Error saving notification to database: $e")
                }
            }
            Timber.d("Notification posted from: $packageName")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn?.let { notification ->
            Timber.d("Notification removed from: ${notification.packageName}")
        }
    }

    private fun getAppName(packageName: String): String =
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }

    fun blockNotificationsFromPackage(packageName: String) {
        addBlockedPackage(packageName)

        // Cancel any existing notifications from this package
        activeNotifications?.forEach { notification ->
            if (notification.packageName == packageName) {
                cancelNotification(notification.key)
            }
        }
    }

    fun unblockNotificationsFromPackage(packageName: String) {
        removeBlockedPackage(packageName)
    }
}