package pion.tech.pionbase.feature.runningapps.presentation

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
                    val apps =
                        withContext(Dispatchers.IO) {
                            getRunningApplications()
                        }
                    _runningApps.value = apps
                } catch (e: Exception) {
                    // Handle error
                    _runningApps.value = emptyList()
                } finally {
                    _isLoading.value = false
                }
            }
        }

        private suspend fun getRunningApplications(): List<RunningAppUIModel> =
            withContext(Dispatchers.IO) {
                val packageManager = context.packageManager
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val runningApps = mutableListOf<RunningAppUIModel>()

                try {
                    // Lấy danh sách tất cả app đã cài đặt và kiểm tra xem có đang chạy không
                    val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

                    for (appInfo in installedApps) {
                        try {
                            val packageName = appInfo.packageName

                            // Kiểm tra xem app có đang chạy không bằng cách kiểm tra process
                            val isRunning = isAppRunning(packageName, activityManager)

                            if (isRunning) {
                                val appName = packageManager.getApplicationLabel(appInfo).toString()
                                val appIcon =
                                    try {
                                        packageManager.getApplicationIcon(appInfo)
                                    } catch (e: Exception) {
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
                                    )

                                runningApps.add(runningApp)
                            }
                        } catch (e: Exception) {
                            // Skip app nếu có lỗi
                            continue
                        }
                    }

                    // Sắp xếp theo memory usage giảm dần
                    runningApps.sortedByDescending { it.memoryUsage }
                } catch (e: Exception) {
                    emptyList()
                }
            }

        private fun isAppRunning(
            packageName: String,
            activityManager: ActivityManager,
        ): Boolean =
            try {
                // Phương pháp 1: Kiểm tra running services
                val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)
                val hasRunningService =
                    runningServices.any { service ->
                        service.service.packageName == packageName
                    }

                // Phương pháp 2: Kiểm tra running processes (chỉ hoạt động cho app hiện tại từ API 21+)
                val runningProcesses = activityManager.runningAppProcesses ?: emptyList()
                val hasRunningProcess =
                    runningProcesses.any { process ->
                        process.processName.startsWith(packageName)
                    }

                // Phương pháp 3: Sử dụng UsageStatsManager (cần permission PACKAGE_USAGE_STATS)
                val hasRecentUsage = checkRecentUsage(packageName)

                hasRunningService || hasRunningProcess || hasRecentUsage
            } catch (e: Exception) {
                false
            }

        private fun checkRecentUsage(packageName: String): Boolean =
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
                    val endTime = System.currentTimeMillis()
                    val startTime = endTime - (1000 * 60 * 10) // Last 10 minutes

                    val usageStats =
                        usageStatsManager.queryUsageStats(
                            android.app.usage.UsageStatsManager.INTERVAL_BEST,
                            startTime,
                            endTime,
                        )

                    usageStats.any { it.packageName == packageName && it.lastTimeUsed > startTime }
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }

        private fun getAppMemoryUsage(
            packageName: String,
            activityManager: ActivityManager,
        ): Long {
            return try {
                val runningProcesses = activityManager.runningAppProcesses ?: return 0L
                val targetProcesses =
                    runningProcesses.filter {
                        it.processName.startsWith(packageName)
                    }

                if (targetProcesses.isNotEmpty()) {
                    val pids = targetProcesses.map { it.pid }.toIntArray()
                    val memInfoArray = activityManager.getProcessMemoryInfo(pids)

                    // Tổng memory của tất cả processes của app
                    memInfoArray.sumOf { it.totalPss.toLong() } * 1024L // Convert to bytes
                } else {
                    0L
                }
            } catch (e: Exception) {
                0L
            }
        }

        fun refreshApps() {
            loadRunningApps()
        }
    }
