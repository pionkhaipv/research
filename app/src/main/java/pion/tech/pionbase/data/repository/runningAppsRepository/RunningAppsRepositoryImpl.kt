package pion.tech.pionbase.data.repository.runningAppsRepository

import android.app.ActivityManager
import android.app.WallpaperManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import pion.tech.pionbase.data.model.runningApp.BackgroundType
import pion.tech.pionbase.data.model.runningApp.RunningAppDtoModel
import pion.tech.pionbase.util.Result

class RunningAppsRepositoryImpl(
    @ApplicationContext private val context: Context,
) : RunningAppsRepository {
    override fun getRunningApps(): Flow<Result<List<RunningAppDtoModel>>> =
        flow {
            try {
                val apps = getBackgroundRunningApplications()
                emit(Result.Success(apps))
            } catch (exception: Exception) {
                emit(Result.Error(exception))
            }
        }.flowOn(Dispatchers.IO)

    override fun refreshRunningApps(): Flow<Result<List<RunningAppDtoModel>>> = getRunningApps()

    private suspend fun getBackgroundRunningApplications(): List<RunningAppDtoModel> =
        withContext(Dispatchers.IO) {
            try {
                val systemServices = getSystemServices()
                val installedApps = getInstalledApplications(systemServices.packageManager)
                Log.d("asgawggawagwgaw", "getBackgroundRunningApplications: $installedApps")
                val runningApps = processInstalledApps(installedApps, systemServices)
                Log.d("asgawggawagwgaw", "runningApps: $runningApps")
                sortRunningApps(runningApps)
            } catch (_: Exception) {
                emptyList()
            }
        }

    /**
     * Data class to hold system services for easier parameter passing
     */
    private data class SystemServices(
        val packageManager: PackageManager,
        val activityManager: ActivityManager,
    )

    /**
     * Get system services needed for app detection
     */
    private fun getSystemServices(): SystemServices {
        val packageManager = context.packageManager
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return SystemServices(packageManager, activityManager)
    }

    /**
     * Get list of all installed applications
     */
    private fun getInstalledApplications(packageManager: PackageManager): List<ApplicationInfo> =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    /**
     * Process all installed apps and filter running ones
     */
    private fun processInstalledApps(
        installedApps: List<ApplicationInfo>,
        systemServices: SystemServices,
    ): List<RunningAppDtoModel> {
        val runningApps = mutableListOf<RunningAppDtoModel>()

        for (appInfo in installedApps) {
            try {
                val runningApp = processInstalledApp(appInfo, systemServices)
                runningApp?.let { runningApps.add(it) }
            } catch (_: Exception) {
                // Skip app if error occurs during processing
                continue
            }
        }

        return runningApps
    }

    /**
     * Process a single installed app and create RunningAppDtoModel if it's running
     */
    private fun processInstalledApp(
        appInfo: ApplicationInfo,
        systemServices: SystemServices,
    ): RunningAppDtoModel? {
        val packageName = appInfo.packageName
        val backgroundInfo = isAppRunningInBackground(packageName, systemServices.activityManager)

        return if (backgroundInfo.isRunning) {
            createRunningAppModel(appInfo, backgroundInfo, systemServices)
        } else {
            null
        }
    }

    /**
     * Create RunningAppDtoModel from app info and background detection results
     */
    private fun createRunningAppModel(
        appInfo: ApplicationInfo,
        backgroundInfo: BackgroundInfo,
        systemServices: SystemServices,
    ): RunningAppDtoModel {
        val packageName = appInfo.packageName
        val appName = getAppName(appInfo, systemServices.packageManager)
        val appIcon = getAppIcon(appInfo, systemServices.packageManager)
        val isSystemApp = isSystemApp(appInfo)
        val memoryUsage = getAppMemoryUsage(packageName, systemServices.activityManager)

        return RunningAppDtoModel(
            packageName = packageName,
            appName = appName,
            appIcon = appIcon,
            isSystemApp = isSystemApp,
            memoryUsage = memoryUsage,
            backgroundType = backgroundInfo.type,
        )
    }

    /**
     * Get app name safely with error handling
     */
    private fun getAppName(
        appInfo: ApplicationInfo,
        packageManager: PackageManager,
    ): String =
        try {
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            appInfo.packageName
        }

    /**
     * Get app icon safely with error handling
     */
    private fun getAppIcon(
        appInfo: ApplicationInfo,
        packageManager: PackageManager,
    ): android.graphics.drawable.Drawable? =
        try {
            packageManager.getApplicationIcon(appInfo)
        } catch (_: Exception) {
            null
        }

    /**
     * Check if app is a system app
     */
    private fun isSystemApp(appInfo: ApplicationInfo): Boolean = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

    /**
     * Sort running apps by priority and memory usage
     */
    private fun sortRunningApps(runningApps: List<RunningAppDtoModel>): List<RunningAppDtoModel> =
        runningApps.sortedWith(
            compareByDescending<RunningAppDtoModel> { it.backgroundType.priority }
                .thenByDescending { it.memoryUsage },
        )

    private data class BackgroundInfo(
        val isRunning: Boolean,
        val type: BackgroundType,
    )

    private fun isAppRunningInBackground(
        packageName: String,
        activityManager: ActivityManager,
    ): BackgroundInfo {
        try {
            // Check for wallpaper services first (highest priority)
            val wallpaperServiceInfo = checkForWallpaperServices(packageName)
            if (wallpaperServiceInfo.isRunning) {
                return wallpaperServiceInfo
            }

            // Check for foreground services (second priority)
            val foregroundServiceInfo = checkForForegroundServices(packageName, activityManager)
            if (foregroundServiceInfo.isRunning) {
                return foregroundServiceInfo
            }

            // Check for background services (third priority)
            val backgroundServiceInfo = checkForBackgroundServices(packageName, activityManager)
            if (backgroundServiceInfo.isRunning) {
                return backgroundServiceInfo
            }

            // Check for active processes (fourth priority)
            val activeProcessInfo = checkForActiveProcesses(packageName, activityManager)
            if (activeProcessInfo.isRunning) {
                return activeProcessInfo
            }

            // Check for recent usage (lowest priority)
            val recentUsageInfo = checkForRecentUsage(packageName)
            if (recentUsageInfo.isRunning) {
                return recentUsageInfo
            }

            return BackgroundInfo(false, BackgroundType.NONE)
        } catch (_: Exception) {
            return BackgroundInfo(false, BackgroundType.NONE)
        }
    }

    private fun checkForForegroundServices(
        packageName: String,
        activityManager: ActivityManager,
    ): BackgroundInfo = checkForServices(packageName, activityManager, isForeground = true)

    private fun checkForBackgroundServices(
        packageName: String,
        activityManager: ActivityManager,
    ): BackgroundInfo = checkForServices(packageName, activityManager, isForeground = false)

    /**
     * Check for running services (foreground or background) for a specific package
     */
    private fun checkForServices(
        packageName: String,
        activityManager: ActivityManager,
        isForeground: Boolean,
    ): BackgroundInfo =
        try {
            val runningServices = getRunningServices(activityManager)
            val serviceInfo = findServiceForPackage(runningServices, packageName, isForeground)
            serviceInfo ?: BackgroundInfo(false, BackgroundType.NONE)
        } catch (_: Exception) {
            BackgroundInfo(false, BackgroundType.NONE)
        }

    /**
     * Get running services from ActivityManager
     */
    @Suppress("DEPRECATION")
    private fun getRunningServices(activityManager: ActivityManager): List<ActivityManager.RunningServiceInfo> =
        activityManager.getRunningServices(Integer.MAX_VALUE)

    /**
     * Find service for specific package with foreground/background filter
     */
    private fun findServiceForPackage(
        runningServices: List<ActivityManager.RunningServiceInfo>,
        packageName: String,
        isForeground: Boolean,
    ): BackgroundInfo? {
        for (service in runningServices) {
            if (service.service.packageName == packageName && service.foreground == isForeground) {
                val backgroundType =
                    if (isForeground) {
                        BackgroundType.FOREGROUND_SERVICE
                    } else {
                        BackgroundType.BACKGROUND_SERVICE
                    }
                return BackgroundInfo(true, backgroundType)
            }
        }
        return null
    }

    /**
     * Check for active background processes for a specific package
     */
    private fun checkForActiveProcesses(
        packageName: String,
        activityManager: ActivityManager,
    ): BackgroundInfo =
        try {
            val runningProcesses = getRunningProcesses(activityManager)
            val hasBackgroundProcess =
                findBackgroundProcessForPackage(runningProcesses, packageName)

            if (hasBackgroundProcess) {
                BackgroundInfo(true, BackgroundType.ACTIVE_PROCESS)
            } else {
                BackgroundInfo(false, BackgroundType.NONE)
            }
        } catch (_: Exception) {
            BackgroundInfo(false, BackgroundType.NONE)
        }

    /**
     * Get running processes from ActivityManager
     */
    @Suppress("DEPRECATION")
    private fun getRunningProcesses(activityManager: ActivityManager): List<ActivityManager.RunningAppProcessInfo> =
        activityManager.runningAppProcesses ?: emptyList()

    /**
     * Find background process for specific package (excluding foreground processes)
     */
    private fun findBackgroundProcessForPackage(
        runningProcesses: List<ActivityManager.RunningAppProcessInfo>,
        packageName: String,
    ): Boolean =
        runningProcesses.any { process ->
            process.processName.startsWith(packageName) &&
                process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }

    /**
     * Check for recent usage of a specific package (last 15 minutes)
     */
    private fun checkForRecentUsage(packageName: String): BackgroundInfo =
        try {
            if (isUsageStatsAvailable()) {
                val usageStatsManager = getUsageStatsManager()
                val hasRecentUsage = checkRecentUsageStats(usageStatsManager, packageName)

                if (hasRecentUsage) {
                    BackgroundInfo(true, BackgroundType.RECENT_USAGE)
                } else {
                    BackgroundInfo(false, BackgroundType.NONE)
                }
            } else {
                BackgroundInfo(false, BackgroundType.NONE)
            }
        } catch (_: Exception) {
            BackgroundInfo(false, BackgroundType.NONE)
        }

    /**
     * Check if usage stats are available (Android 5.0+)
     */
    private fun isUsageStatsAvailable(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    /**
     * Get UsageStatsManager service
     */
    private fun getUsageStatsManager(): UsageStatsManager? = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

    /**
     * Check if package has been used recently (last 15 minutes)
     */
    private fun checkRecentUsageStats(
        usageStatsManager: UsageStatsManager?,
        packageName: String,
    ): Boolean {
        if (usageStatsManager == null) return false

        val timeRange = getRecentUsageTimeRange()
        val usageStats =
            usageStatsManager
                .queryUsageStats(
                    UsageStatsManager.INTERVAL_BEST,
                    timeRange.startTime,
                    timeRange.endTime,
                ).toList()

        return usageStats.any { stats ->
            stats.packageName == packageName && stats.lastTimeUsed > timeRange.startTime
        }
    }

    /**
     * Data class for time range
     */
    private data class TimeRange(
        val startTime: Long,
        val endTime: Long,
    )

    /**
     * Get time range for recent usage check (last 15 minutes)
     */
    private fun getRecentUsageTimeRange(): TimeRange {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (15 * 60 * 1000L) // 15 minutes in milliseconds
        return TimeRange(startTime, endTime)
    }

    /**
     * Check if package provides the currently active live wallpaper service
     */
    private fun checkForWallpaperServices(packageName: String): BackgroundInfo =
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val isActiveWallpaper = isActiveWallpaperService(wallpaperManager, packageName)

            if (isActiveWallpaper) {
                BackgroundInfo(true, BackgroundType.WALLPAPER_SERVICE)
            } else {
                BackgroundInfo(false, BackgroundType.NONE)
            }
        } catch (_: Exception) {
            BackgroundInfo(false, BackgroundType.NONE)
        }

    /**
     * Check if package is the active wallpaper service
     */
    private fun isActiveWallpaperService(
        wallpaperManager: WallpaperManager,
        packageName: String,
    ): Boolean {
        val wallpaperInfo = wallpaperManager.wallpaperInfo
        return wallpaperInfo?.packageName == packageName
    }

    /**
     * Calculate total memory usage for a specific package using PSS (Proportional Set Size)
     */
    private fun getAppMemoryUsage(
        packageName: String,
        activityManager: ActivityManager,
    ): Long =
        try {
            val runningProcesses = getRunningProcesses(activityManager)
            val targetProcesses = getProcessesForPackage(runningProcesses, packageName)
            calculateTotalMemoryUsage(targetProcesses, activityManager)
        } catch (_: Exception) {
            0L
        }

    /**
     * Get processes that belong to a specific package
     */
    private fun getProcessesForPackage(
        runningProcesses: List<ActivityManager.RunningAppProcessInfo>,
        packageName: String,
    ): List<ActivityManager.RunningAppProcessInfo> =
        runningProcesses.filter { process ->
            process.processName.startsWith(packageName)
        }

    /**
     * Calculate total memory usage for a list of processes
     */
    private fun calculateTotalMemoryUsage(
        processes: List<ActivityManager.RunningAppProcessInfo>,
        activityManager: ActivityManager,
    ): Long {
        if (processes.isEmpty()) return 0L

        val pids = extractProcessIds(processes)
        return if (pids.isNotEmpty()) {
            calculateMemoryFromPids(pids, activityManager)
        } else {
            0L
        }
    }

    /**
     * Extract process IDs from running processes
     */
    private fun extractProcessIds(processes: List<ActivityManager.RunningAppProcessInfo>): IntArray = processes.map { it.pid }.toIntArray()

    /**
     * Calculate memory usage from process IDs using PSS measurement
     */
    private fun calculateMemoryFromPids(
        pids: IntArray,
        activityManager: ActivityManager,
    ): Long {
        val memoryInfoArray = activityManager.getProcessMemoryInfo(pids)
        var totalMemory = 0L

        for (memoryInfo in memoryInfoArray) {
            // PSS (Proportional Set Size) is the most accurate memory measurement
            totalMemory += convertKbToBytes(memoryInfo.totalPss)
        }

        return totalMemory
    }

    /**
     * Convert memory from KB to bytes
     */
    private fun convertKbToBytes(memoryInKb: Int): Long = memoryInKb * 1024L
}
