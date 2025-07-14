package pion.tech.pionbase.feature.runningapps.presentation

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pion.tech.pionbase.feature.runningapps.presentation.model.BackgroundType
import pion.tech.pionbase.feature.runningapps.presentation.model.RunningAppUIModel
import javax.inject.Inject

@HiltViewModel
class RunningAppsViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : BaseViewModel() {
        private val _runningApps = MutableStateFlow<List<RunningAppUIModel>>(emptyList())
        val runningApps: StateFlow<List<RunningAppUIModel>> = _runningApps.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        init {
            loadRunningApps()
        }

        fun loadRunningApps() {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val apps = getBackgroundRunningApplications()
                    _runningApps.value = apps
                } catch (_: Exception) {
                    // Handle error
                    _runningApps.value = emptyList()
                } finally {
                    _isLoading.value = false
                }
            }
        }

        private suspend fun getBackgroundRunningApplications(): List<RunningAppUIModel> =
            withContext(Dispatchers.IO) {
                val packageManager = context.packageManager
                val activityManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val runningApps = mutableListOf<RunningAppUIModel>()

                try {
                    // Lấy danh sách tất cả app đã cài đặt
                    val installedApps =
                        packageManager.getInstalledApplications(PackageManager.GET_META_DATA).toList()

                    for (appInfo in installedApps) {
                        try {
                            val packageName = appInfo.packageName

                            // Kiểm tra app có đang chạy ẩn không
                            val backgroundInfo = isAppRunningInBackground(packageName, activityManager)

                            if (backgroundInfo.isRunning) {
                                val appName = packageManager.getApplicationLabel(appInfo).toString()
                                val appIcon =
                                    try {
                                        packageManager.getApplicationIcon(appInfo)
                                    } catch (_: Exception) {
                                        null
                                    }
                                val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                                // Lấy memory usage
                                val memoryUsage = getAppMemoryUsage(packageName, activityManager)

                                val runningApp =
                                    RunningAppUIModel(
                                        packageName = packageName,
                                        appName = appName,
                                        appIcon = appIcon,
                                        isSystemApp = isSystemApp,
                                        memoryUsage = memoryUsage,
                                        backgroundType = backgroundInfo.type,
                                    )

                                runningApps.add(runningApp)
                            }
                        } catch (_: Exception) {
                            // Skip app nếu có lỗi
                            continue
                        }
                    }

                    // Sắp xếp theo độ ưu tiên
                    runningApps.sortedWith(
                        compareByDescending<RunningAppUIModel> { it.backgroundType.priority }
                            .thenByDescending { it.memoryUsage },
                    )
                } catch (_: Exception) {
                    emptyList()
                }
            }

        private data class BackgroundInfo(
            val isRunning: Boolean,
            val type: BackgroundType,
        )

        private fun isAppRunningInBackground(
            packageName: String,
            activityManager: ActivityManager,
        ): BackgroundInfo {
            try {
                val tag = "isAppRunningInBackground"

                // 1. Kiểm tra Foreground Services (có notification)
                @Suppress("DEPRECATION")
                val services = activityManager.getRunningServices(Integer.MAX_VALUE).toList()
                val hasForegroundService =
                    services.any { service ->
                        service.service.packageName == packageName && service.foreground
                    }
                if (hasForegroundService) {
                    Log.d(tag, "hasForegroundService: $packageName")
                    return BackgroundInfo(true, BackgroundType.FOREGROUND_SERVICE)
                }

                // 2. Kiểm tra Background Services (traditional services)
                val hasBackgroundService =
                    services.any { service ->
                        service.service.packageName == packageName ||
                            service.process?.startsWith(packageName) == true ||
                            service.process?.contains(packageName) == true
                    }
                if (hasBackgroundService) {
                    Log.d(tag, "hasBackgroundService: $packageName")
                    return BackgroundInfo(true, BackgroundType.BACKGROUND_SERVICE)
                }

                // 3. Kiểm tra System Services đặc biệt (chỉ các services thực sự)
                val hasSystemService = checkSystemServices(packageName)
                if (hasSystemService) {
                    Log.d(tag, "hasSystemService: $packageName")
                    return BackgroundInfo(true, BackgroundType.BACKGROUND_SERVICE)
                }

                // 4. Kiểm tra Job Scheduler Services (scheduled services)
                val hasJobService = checkJobSchedulerServices(packageName)
                if (hasJobService) {
                    Log.d(tag, "hasJobService: $packageName")
                    return BackgroundInfo(true, BackgroundType.BACKGROUND_SERVICE)
                }

                // 5. Kiểm tra WorkManager Jobs (background job services)
                val hasWorkManagerJob = checkWorkManagerJobs(packageName)
                if (hasWorkManagerJob) {
                    Log.d(tag, "hasWorkManagerJob: $packageName")
                    return BackgroundInfo(true, BackgroundType.BACKGROUND_SERVICE)
                }

                return BackgroundInfo(false, BackgroundType.NONE)
            } catch (_: Exception) {
                return BackgroundInfo(false, BackgroundType.NONE)
            }
        }

        /**
         * Kiểm tra app có thực sự đang chạy không (loại trừ force stopped apps)
         */
        private fun isAppActuallyRunning(
            packageName: String,
            activityManager: ActivityManager,
        ): Boolean {
            try {
                // 1. Kiểm tra có running processes không
                val runningProcesses = activityManager.runningAppProcesses?.toList() ?: emptyList()
                val hasRunningProcess =
                    runningProcesses.any { process ->
                        process.processName.startsWith(packageName) ||
                            process.processName.contains(packageName) ||
                            process.pkgList?.contains(packageName) == true
                    }

                // 2. Kiểm tra có running services không
                @Suppress("DEPRECATION")
                val services = activityManager.getRunningServices(Integer.MAX_VALUE).toList()
                val hasRunningService =
                    services.any { service ->
                        service.service.packageName == packageName ||
                            service.process?.startsWith(packageName) == true ||
                            service.process?.contains(packageName) == true
                    }

                // 3. Kiểm tra system services đang active
                val hasActiveSystemService = checkSystemServices(packageName)

                // 4. Kiểm tra scheduled jobs
                val hasScheduledJobs =
                    checkJobSchedulerServices(packageName) || checkWorkManagerJobs(packageName)

                // App chỉ được coi là "running" nếu có ít nhất một điều kiện trên
                return hasRunningProcess || hasRunningService || hasActiveSystemService || hasScheduledJobs
            } catch (_: Exception) {
                return false
            }
        }

        private fun checkSystemServices(packageName: String): Boolean {
            try {
                // Kiểm tra các system services
                if (checkAccessibilityServices(packageName)) return true
                if (checkDeviceAdminServices(packageName)) return true
                if (checkWallpaperServices(packageName)) return true
                if (checkNotificationListenerServices(packageName)) return true
                if (checkDreamServices(packageName)) return true
                if (checkTileServices(packageName)) return true
                if (checkVoiceInteractionServices(packageName)) return true
                if (checkAutofillServices(packageName)) return true

                return false
            } catch (_: Exception) {
                return false
            }
        }

        private val tagRunningService = "tagRunningService"

        private fun checkAccessibilityServices(packageName: String): Boolean =
            try {
                val enabledServices =
                    android.provider.Settings.Secure.getString(
                        context.contentResolver,
                        android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                    )
                val isHaveService = enabledServices?.contains(packageName) == true
                Log.d(tagRunningService, "checkAccessibilityServices: $packageName $isHaveService")
                isHaveService
            } catch (_: Exception) {
                false
            }

        private fun checkDeviceAdminServices(packageName: String): Boolean =
            try {
                val devicePolicyManager =
                    context.getSystemService(Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
                val activeAdmins = devicePolicyManager.activeAdmins
                val isHaveService = activeAdmins?.any { it.packageName == packageName } == true
                Log.d(tagRunningService, "checkDeviceAdminServices: $packageName $isHaveService")
                isHaveService
            } catch (_: Exception) {
                false
            }

        private fun checkWallpaperServices(packageName: String): Boolean =
            try {
                val wallpaperManager = android.app.WallpaperManager.getInstance(context)
                val wallpaperInfo = wallpaperManager.wallpaperInfo
                val isHaveService = wallpaperInfo?.packageName == packageName
                Log.d(tagRunningService, "checkWallpaperServices: $packageName $isHaveService")
                isHaveService
            } catch (_: Exception) {
                false
            }

        private fun checkNotificationListenerServices(packageName: String): Boolean =
            try {
                val enabledListeners =
                    android.provider.Settings.Secure.getString(
                        context.contentResolver,
                        "enabled_notification_listeners",
                    )
                val isHaveService = enabledListeners?.contains(packageName) == true
                Log.d(tagRunningService, "checkNotificationListenerServices: $packageName $isHaveService")
                isHaveService
            } catch (_: Exception) {
                false
            }

        private fun checkJobSchedulerServices(packageName: String): Boolean =
            try {
                val jobScheduler =
                    context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as android.app.job.JobScheduler
                val pendingJobs = jobScheduler.allPendingJobs.toList()
                val isHaveService =
                    pendingJobs.any { job ->
                        job.service.packageName == packageName
                    }
                Log.d(tagRunningService, "checkJobSchedulerServices: $packageName $isHaveService")
                isHaveService
            } catch (_: Exception) {
                false
            }

        private fun checkWorkManagerJobs(packageName: String): Boolean =
            try {
                val jobScheduler =
                    context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as android.app.job.JobScheduler
                val pendingJobs = jobScheduler.allPendingJobs.toList()
                val isHaveService =
                    pendingJobs.any { job ->
                        val componentName = job.service
                        componentName.packageName == packageName ||
                            componentName.className.contains("WorkManagerJobService") ||
                            componentName.className.contains("SystemJobService")
                    }
                Log.d(tagRunningService, "checkWorkManagerJobs: $packageName $isHaveService")
                isHaveService
            } catch (_: Exception) {
                false
            }

        private fun checkDreamServices(packageName: String): Boolean =
            try {
                val enabledDreams =
                    android.provider.Settings.Secure.getString(
                        context.contentResolver,
                        "enabled_dreams",
                    )
                val isHaveService = enabledDreams?.contains(packageName) == true
                Log.d(tagRunningService, "checkDreamServices: $packageName $isHaveService")
                isHaveService
            } catch (_: Exception) {
                false
            }

        private fun checkTileServices(packageName: String): Boolean =
            try {
                val packageManager = context.packageManager
                val services =
                    packageManager
                        .queryIntentServices(
                            android.content.Intent(android.service.quicksettings.TileService.ACTION_QS_TILE),
                            PackageManager.GET_META_DATA,
                        ).toList()
                val isHaveService = services.any { it.serviceInfo.packageName == packageName }
                Log.d(tagRunningService, "checkTileServices: $packageName $isHaveService")
                isHaveService
            } catch (_: Exception) {
                false
            }

        private fun checkVoiceInteractionServices(packageName: String): Boolean =
            try {
                val voiceInteractionService =
                    android.provider.Settings.Secure.getString(
                        context.contentResolver,
                        "voice_interaction_service",
                    )
                val isHaveService = voiceInteractionService?.contains(packageName) == true
                Log.d(tagRunningService, "checkVoiceInteractionServices: $packageName $isHaveService")
                isHaveService
            } catch (_: Exception) {
                false
            }

        private fun checkAutofillServices(packageName: String): Boolean =
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val autofillService =
                        android.provider.Settings.Secure.getString(
                            context.contentResolver,
                            "autofill_service",
                        )
                    val isHaveService = autofillService?.contains(packageName) == true
                    Log.d(tagRunningService, "checkAutofillServices: $packageName $isHaveService")
                    isHaveService
                } else {
                    false
                }
            } catch (_: Exception) {
                false
            }

        private fun getAppMemoryUsage(
            packageName: String,
            activityManager: ActivityManager,
        ): Long {
            return try {
                val runningProcesses = activityManager.runningAppProcesses?.toList() ?: return 0L
                val targetProcesses =
                    runningProcesses.filter {
                        it.processName.startsWith(packageName)
                    }

                if (targetProcesses.isNotEmpty()) {
                    val pids = targetProcesses.map { it.pid }.toIntArray()
                    val memInfoArray = activityManager.getProcessMemoryInfo(pids)
                    memInfoArray.sumOf { it.totalPss.toLong() } * 1024L
                } else {
                    0L
                }
            } catch (_: Exception) {
                0L
            }
        }

        fun refreshApps() {
            loadRunningApps()
        }
    }
