package pion.tech.pionbase.feature.notifications.presentation

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.presentation.CommonViewModel
import pion.tech.pionbase.databinding.FragmentNotificationManagerBinding
import pion.tech.pionbase.feature.notifications.presentation.adapter.NotificationAppAdapter
import pion.tech.pionbase.feature.notifications.presentation.model.NotificationAppInfo
import pion.tech.pionbase.util.displayToast

@AndroidEntryPoint
class NotificationManagerFragment :
    BaseFragment<FragmentNotificationManagerBinding, NotificationManagerViewModel, CommonViewModel>(
        FragmentNotificationManagerBinding::inflate,
        NotificationManagerViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    private lateinit var adapter: NotificationAppAdapter

    override fun init(view: View) {
        logger.logScreen("notification_manager_show")
        logger.logEvent("notification_manager_view")

        checkNotificationPermission()
        initView()
        loadNotificationApps()
    }

    override fun subscribeObserver(view: View) {
        // Observe ViewModel data if needed
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API 33+), we need POST_NOTIFICATIONS permission
            val hasPermission = NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
            if (!hasPermission) {
                showNotificationPermissionDialog()
            }
        } else {
            // For older versions, check if notification listener access is enabled
            if (!isNotificationServiceEnabled()) {
                showNotificationListenerPermissionDialog()
            }
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val packageName = requireContext().packageName
        val flat =
            Settings.Secure.getString(
                requireContext().contentResolver,
                "enabled_notification_listeners",
            )
        if (flat != null && flat.isNotEmpty()) {
            val names = flat.split(":").toTypedArray()
            for (name in names) {
                if (name.contains(packageName)) {
                    return true
                }
            }
        }
        return false
    }

    private fun showNotificationPermissionDialog() {
        val builder =
            androidx.appcompat.app.AlertDialog
                .Builder(requireContext())
        builder.setTitle("Notification Access Required")
        builder.setMessage(
            "To manage app notifications, this app needs notification access permission. Please grant the permission in the next screen.",
        )
        builder.setPositiveButton("Grant Permission") { _, _ ->
            requestNotificationPermission()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            navigator.navigateUp()
        }
        builder.show()
    }

    private fun showNotificationListenerPermissionDialog() {
        val builder =
            androidx.appcompat.app.AlertDialog
                .Builder(requireContext())
        builder.setTitle("Notification Listener Access Required")
        builder.setMessage(
            "To read and manage app notifications, this app needs notification listener access. Please enable it in the next screen.",
        )
        builder.setPositiveButton("Grant Permission") { _, _ ->
            requestNotificationListenerPermission()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            navigator.navigateUp()
        }
        builder.show()
    }

    private fun requestNotificationPermission() {
        try {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            } else {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = android.net.Uri.parse("package:${requireContext().packageName}")
            }
            startActivity(intent)
        } catch (_: Exception) {
            displayToast("Cannot open notification settings")
        }
    }

    private fun requestNotificationListenerPermission() {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
        } catch (_: Exception) {
            displayToast("Cannot open notification listener settings")
        }
    }

    private fun initView() {
        adapter =
            NotificationAppAdapter { app ->
                showAppNotificationDetails(app)
            }

        binding.rvNotificationApps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NotificationManagerFragment.adapter
        }

        binding.btnRefresh.setOnClickListener {
            loadNotificationApps()
        }
    }

    private fun loadNotificationApps() {
        binding.progressBar.visibility = View.VISIBLE

        val apps = getAppsWithNotificationInfo()
        adapter.submitList(apps)

        binding.progressBar.visibility = View.GONE
        binding.tvTotalApps.text = "Total apps: ${apps.size}"
        binding.tvEnabledApps.text = "Notifications enabled: ${apps.count { it.notificationsEnabled }}"
    }

    private fun getAppsWithNotificationInfo(): List<NotificationAppInfo> {
        val context = requireContext()
        val packageManager = context.packageManager
        val apps = mutableListOf<NotificationAppInfo>()

        try {
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            for (appInfo in installedApps) {
                // Skip system apps (optional - can be removed to include system apps)
                if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                    continue
                }

                val packageName = appInfo.packageName
                val appName = packageManager.getApplicationLabel(appInfo).toString()

                // Check if notifications are enabled for this specific app
                val notificationsEnabled = checkNotificationEnabledForApp(packageName)

                // Get app icon
                val appIcon =
                    try {
                        packageManager.getApplicationIcon(appInfo)
                    } catch (_: PackageManager.NameNotFoundException) {
                        null
                    }

                apps.add(
                    NotificationAppInfo(
                        packageName = packageName,
                        appName = appName,
                        appIcon = appIcon,
                        notificationsEnabled = notificationsEnabled,
                    ),
                )
            }
        } catch (e: Exception) {
            logger.logEvent("error_loading_notification_apps: ${e.message}")
        }

        return apps.sortedBy { it.appName }
    }

    /**
     * Kiểm tra trạng thái thông báo của một app cụ thể
     */
    private fun checkNotificationEnabledForApp(packageName: String): Boolean =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Đối với Android 8.0+ (API 26+)
                val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Tạo context cho app cụ thể để kiểm tra
                val appContext = requireContext().createPackageContext(packageName, 0)
                val appNotificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Kiểm tra xem thông báo có được bật cho app này không
                appNotificationManager.areNotificationsEnabled()
            } else {
                // Đối với Android cũ hơn, sử dụng NotificationManagerCompat
                val appContext = requireContext().createPackageContext(packageName, 0)
                NotificationManagerCompat.from(appContext).areNotificationsEnabled()
            }
        } catch (e: Exception) {
            // Nếu không thể kiểm tra, mặc định là true
            true
        }

    private fun showAppNotificationDetails(app: NotificationAppInfo) {
        val builder =
            androidx.appcompat.app.AlertDialog
                .Builder(requireContext())
        builder.setTitle(app.appName)

        val message =
            buildString {
                append("Package: ${app.packageName}\n")
                append("Notifications: ${if (app.notificationsEnabled) "Enabled" else "Disabled"}\n\n")
                append("Tap 'Open Settings' to manage this app's notification settings.")
            }

        builder.setMessage(message)
        builder.setPositiveButton("Open Settings") { _, _ ->
            openAppNotificationSettings(app.packageName)
        }
        builder.setNegativeButton("Close", null)
        builder.show()
    }

    private fun openAppNotificationSettings(packageName: String) {
        try {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            } else {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = android.net.Uri.parse("package:$packageName")
            }
            startActivity(intent)
        } catch (_: Exception) {
            displayToast("Cannot open notification settings for this app")
        }
    }
}
