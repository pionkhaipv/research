package pion.tech.pionbase.feature.permissions.presentation

import android.Manifest
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
import pion.tech.pionbase.feature.permissions.presentation.model.AppPermissionUIModel
import pion.tech.pionbase.feature.permissions.presentation.model.DangerLevel
import pion.tech.pionbase.feature.permissions.presentation.model.PermissionUIModel
import javax.inject.Inject

@HiltViewModel
class DangerousPermissionsViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : BaseViewModel() {
        private val _permissions = MutableStateFlow<List<PermissionUIModel>>(emptyList())
        val permissions: StateFlow<List<PermissionUIModel>> = _permissions.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        // Danh sách các quyền nguy hiểm cần theo dõi
        private val dangerousPermissions =
            mapOf(
                Manifest.permission.ACCESS_FINE_LOCATION to Triple("Location Access", "Truy cập vị trí chính xác", DangerLevel.HIGH),
                Manifest.permission.ACCESS_COARSE_LOCATION to Triple("Location Access", "Truy cập vị trí gần đúng", DangerLevel.MEDIUM),
                Manifest.permission.READ_CONTACTS to Triple("Contacts Access", "Đọc danh bạ", DangerLevel.HIGH),
                Manifest.permission.WRITE_CONTACTS to Triple("Contacts Access", "Chỉnh sửa danh bạ", DangerLevel.HIGH),
                Manifest.permission.READ_SMS to Triple("SMS Access", "Đọc tin nhắn SMS", DangerLevel.CRITICAL),
                Manifest.permission.SEND_SMS to Triple("SMS Access", "Gửi tin nhắn SMS", DangerLevel.CRITICAL),
                Manifest.permission.RECEIVE_SMS to Triple("SMS Access", "Nhận tin nhắn SMS", DangerLevel.HIGH),
                Manifest.permission.READ_CALL_LOG to Triple("Call Log Access", "Đọc nhật ký cuộc gọi", DangerLevel.HIGH),
                Manifest.permission.WRITE_CALL_LOG to Triple("Call Log Access", "Chỉnh sửa nhật ký cuộc gọi", DangerLevel.HIGH),
                Manifest.permission.CALL_PHONE to Triple("Phone Access", "Thực hiện cuộc gọi", DangerLevel.CRITICAL),
                Manifest.permission.CAMERA to Triple("Camera Access", "Truy cập camera", DangerLevel.MEDIUM),
                Manifest.permission.RECORD_AUDIO to Triple("Microphone Access", "Ghi âm", DangerLevel.HIGH),
                Manifest.permission.READ_EXTERNAL_STORAGE to Triple("Storage Access", "Đọc bộ nhớ ngoài", DangerLevel.MEDIUM),
                Manifest.permission.WRITE_EXTERNAL_STORAGE to Triple("Storage Access", "Ghi bộ nhớ ngoài", DangerLevel.MEDIUM),
                Manifest.permission.READ_CALENDAR to Triple("Calendar Access", "Đọc lịch", DangerLevel.MEDIUM),
                Manifest.permission.WRITE_CALENDAR to Triple("Calendar Access", "Chỉnh sửa lịch", DangerLevel.MEDIUM),
                Manifest.permission.GET_ACCOUNTS to Triple("Account Access", "Truy cập tài khoản", DangerLevel.MEDIUM),
                Manifest.permission.BODY_SENSORS to Triple("Sensors Access", "Truy cập cảm biến cơ thể", DangerLevel.HIGH),
            )

        init {
            loadDangerousPermissions()
        }

        fun loadDangerousPermissions() {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val permissionsList =
                        withContext(Dispatchers.IO) {
                            getDangerousPermissions()
                        }
                    _permissions.value = permissionsList
                } catch (e: Exception) {
                    _permissions.value = emptyList()
                } finally {
                    _isLoading.value = false
                }
            }
        }

        private suspend fun getDangerousPermissions(): List<PermissionUIModel> =
            withContext(Dispatchers.IO) {
                val packageManager = context.packageManager
                val permissionsList = mutableListOf<PermissionUIModel>()

                // Lấy tất cả app đã cài đặt
                val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

                // Với mỗi quyền nguy hiểm, tìm các app có quyền đó
                for ((permission, info) in dangerousPermissions) {
                    val (displayName, description, dangerLevel) = info
                    val appsWithPermission = mutableListOf<AppPermissionUIModel>()

                    for (appInfo in installedApps) {
                        try {
                            val packageName = appInfo.packageName
                            val appName = packageManager.getApplicationLabel(appInfo).toString()
                            val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                            // Kiểm tra xem app có request permission này không
                            val packageInfo =
                                packageManager.getPackageInfo(
                                    packageName,
                                    PackageManager.GET_PERMISSIONS,
                                )

                            val requestedPermissions = packageInfo.requestedPermissions
                            if (requestedPermissions != null && requestedPermissions.contains(permission)) {
                                // Kiểm tra xem permission có được grant không
                                val isGranted = packageManager.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED

                                val appIcon =
                                    try {
                                        packageManager.getApplicationIcon(appInfo)
                                    } catch (e: Exception) {
                                        null
                                    }

                                appsWithPermission.add(
                                    AppPermissionUIModel(
                                        packageName = packageName,
                                        appName = appName,
                                        appIcon = appIcon,
                                        isSystemApp = isSystemApp,
                                        isGranted = isGranted,
                                    ),
                                )
                            }
                        } catch (e: Exception) {
                            // Skip app nếu có lỗi
                            continue
                        }
                    }

                    // Chỉ thêm permission nếu có ít nhất 1 app sử dụng
                    if (appsWithPermission.isNotEmpty()) {
                        // Sắp xếp: granted apps trước, sau đó theo tên
                        appsWithPermission.sortWith(compareByDescending<AppPermissionUIModel> { it.isGranted }.thenBy { it.appName })

                        permissionsList.add(
                            PermissionUIModel(
                                permissionName = permission,
                                displayName = displayName,
                                description = description,
                                dangerLevel = dangerLevel,
                                appsWithPermission = appsWithPermission,
                            ),
                        )
                    }
                }

                // Sắp xếp theo độ nguy hiểm và số lượng app
                permissionsList.sortedWith(
                    compareByDescending<PermissionUIModel> { it.dangerLevel.ordinal }
                        .thenByDescending { it.appsWithPermission.size },
                )
            }

        fun refreshPermissions() {
            loadDangerousPermissions()
        }
    }
