package pion.tech.pionbase.feature.home.presetation

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.app.presentation.CommonViewModel
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.presetation.adapter.DemoAdapter
import pion.tech.pionbase.feature.home.presetation.dialog.DemoDialog
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

    override fun init(view: View) {
        logger.logScreen("home_show")
        logger.logEvent("home_view")
        initView()
        plusEvent()
        settingEvent()
        onBackEvent()

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
            val installedApps =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

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
                val appOpsManager =
                    context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
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

    private fun initView() {
        binding.rvMain.adapter = adapter
    }

    private fun plusEvent() {
        binding.btnPlus.setOnClickListener {
            viewModel.plusValue()
        }
    }

    private fun settingEvent() {
        binding.btnAdDetector.setOnClickListener {
            navigator.navigateTo(R.id.action_homeFragment_to_popupStatisticsFragment)
        }

        binding.btnRunningApps.setOnClickListener {
            navigateToRunningApps()
        }

        binding.btnDangerousPermissions.setOnClickListener {
            navigator.navigateTo(R.id.action_homeFragment_to_dangerousPermissionsFragment)
        }
    }

    private fun navigateToRunningApps() {
        // Check permission trước khi navigate
        checkUsageStatsPermission()
    }

    private fun checkUsageStatsPermission() {
        val appOpsManager =
            requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                requireContext().packageName,
            )

        if (mode == AppOpsManager.MODE_ALLOWED) {
            // Đã có quyền, navigate trực tiếp
            navigateToRunningAppsScreen()
        } else {
            // Chưa có quyền, hiển thị dialog xin quyền
            showUsageStatsPermissionDialog()
        }
    }

    private fun showUsageStatsPermissionDialog() {
        val builder =
            androidx.appcompat.app.AlertDialog
                .Builder(requireContext())
        builder.setTitle("Usage Stats Permission Required")
        builder.setMessage(
            "To display running apps accurately, this app needs access to usage statistics. Please grant the permission in the next screen.",
        )
        builder.setPositiveButton("Grant Permission") { _, _ ->
            requestUsageStatsPermission()
        }
        builder.setNegativeButton("Continue without permission") { _, _ ->
            navigateToRunningAppsScreen()
        }
        builder.show()
    }

    private fun requestUsageStatsPermission() {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            // Sau khi user quay lại từ settings, navigate đến Running Apps
            navigateToRunningAppsScreen()
        } catch (e: Exception) {
            displayToast("Cannot open usage access settings")
            navigateToRunningAppsScreen()
        }
    }

    private fun navigateToRunningAppsScreen() {
        navigator.navigateTo(R.id.action_homeFragment_to_runningAppsFragment)
    }

    private fun navigateToSetting() {
        // Implement navigation to setting fragment
        navigator.navigateTo(R.id.action_homeFragment_to_settingFragment)
    }

    private fun onBackEvent() {
        // Handle back press if needed
    }
}
