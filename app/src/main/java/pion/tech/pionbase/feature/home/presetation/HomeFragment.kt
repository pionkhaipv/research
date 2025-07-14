package pion.tech.pionbase.feature.home.presetation

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.app.presentation.CommonViewModel
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.presetation.adapter.DemoAdapter
import pion.tech.pionbase.feature.home.presetation.dialog.DemoDialog
import pion.tech.pionbase.util.AccessibilityServiceHelper
import pion.tech.pionbase.util.displayToast

data class AppWithOverlayPermission(
    val packageName: String,
    val appName: String,
    val hasOverlayPermission: Boolean,
    val appIcon: Drawable?,
)

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel, CommonViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class.java,
        CommonViewModel::class.java,
    ),
    DemoDialog.Listener {
    val adapter = DemoAdapter()
    val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        }

    // BroadcastReceiver để nhận thông báo popup

    override fun init(view: View) {
        logger.logScreen("home_show")
        logger.logEvent("home_view")
        initView()
        plusEvent()
        settingEvent()
        onBackEvent()

        // Kiểm tra và setup AccessibilityService
        setupPopupDetection()

        // Test hàm lấy danh sách app có quyền overlay
    }

    override fun subscribeObserver(view: View) {
    }

    override fun onDialogPositiveClick() {
    }

    override fun onDialogNegativeClick() {
        displayToast("Hello")
    }

    /**
     * Lấy danh sách các ứng dụng có quyền ghi đè lên màn hình (overlay permission)
     * @return List<AppWithOverlayPermission> - Danh sách ứng dụng với thông tin quyền overlay
     */
    private fun getAppsWithOverlayPermission(): List<AppWithOverlayPermission> {
        val context = requireContext()
        val packageManager = context.packageManager
        val appsWithOverlay = mutableListOf<AppWithOverlayPermission>()

        try {
            // Lấy danh sách tất cả ứng dụng đã cài đặt
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            for (appInfo in installedApps) {
                // Bỏ qua system apps nếu muốn (có thể comment dòng này nếu muốn bao gồm system apps)
                if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                    continue
                }

                val packageName = appInfo.packageName
                val appName = packageManager.getApplicationLabel(appInfo).toString()

                // Kiểm tra quyền overlay
                val hasOverlayPermission =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkOverlayPermissionForApp(packageName)
                    } else {
                        // Trước Android M, không có quyền overlay riêng biệt
                        true
                    }

                // Lấy biểu tượng ứng dụng
                val appIcon =
                    try {
                        packageManager.getApplicationIcon(appInfo)
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }

                appsWithOverlay.add(
                    AppWithOverlayPermission(
                        packageName = packageName,
                        appName = appName,
                        hasOverlayPermission = hasOverlayPermission,
                        appIcon = appIcon,
                    ),
                )
            }
        } catch (e: Exception) {
            logger.logEvent("error_getting_overlay_apps: ${e.message}")
        }

        return appsWithOverlay
    }

    /**
     * Kiểm tra quyền overlay cho một ứng dụng cụ thể
     * Sử dụng AppOpsManager để kiểm tra chính xác hơn
     */
    private fun checkOverlayPermissionForApp(packageName: String): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val context = requireContext()
                val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
                val packageManager = context.packageManager

                // Lấy UID của ứng dụng
                val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
                val uid = applicationInfo.uid

                // Kiểm tra quyền SYSTEM_ALERT_WINDOW
                val mode =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        appOpsManager.unsafeCheckOpNoThrow(
                            android.app.AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                            uid,
                            packageName,
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        appOpsManager.checkOpNoThrow(
                            android.app.AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                            uid,
                            packageName,
                        )
                    }

                Log.d("OverlayCheck", "App: $packageName, Mode: $mode")
                mode == android.app.AppOpsManager.MODE_ALLOWED
            } catch (e: Exception) {
                Log.e("OverlayCheck", "Error checking overlay permission for $packageName", e)
                false
            }
        } else {
            true
        }

    /**
     * Lấy chỉ danh sách các ứng dụng ĐÃ ĐƯỢC CẤP quyền ghi đè lên màn hình
     * @return List<AppWithOverlayPermission> - Danh sách ứng dụng đã có quyền overlay
     */
    private fun getAppsWithOverlayPermissionGranted(): List<AppWithOverlayPermission> =
        getAppsWithOverlayPermission().filter {
            it.hasOverlayPermission
        }

    /**
     * Kiểm tra một ứng dụng cụ thể có quyền overlay hay không
     * @param packageName - Tên package của ứng dụng cần kiểm tra
     * @return Boolean - true nếu có quyền, false nếu không
     */
    private fun checkAppOverlayPermission(packageName: String): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val context = requireContext()
                Settings.canDrawOverlays(context.createPackageContext(packageName, 0))
            } catch (e: Exception) {
                false
            }
        } else {
            true
        }

    private fun setupPopupDetection() {
        val serviceStatus = AccessibilityServiceHelper.getServiceStatus(requireContext())

        if (!serviceStatus.canDetectPopups) {
            // Hiển thị dialog yêu cầu bật AccessibilityService
            showEnableAccessibilityDialog()
        } else {
            displayToast("Dịch vụ phát hiện popup đã sẵn sàng")
        }
    }

    private fun showEnableAccessibilityDialog() {
        val builder =
            androidx.appcompat.app.AlertDialog
                .Builder(requireContext())
        builder.setTitle(R.string.popup_detection_title)
        builder.setMessage(R.string.accessibility_service_disabled)
        builder.setPositiveButton(R.string.go_to_accessibility_settings) { _, _ ->
            AccessibilityServiceHelper.openAccessibilitySettings(requireContext())
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }
}
