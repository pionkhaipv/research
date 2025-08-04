package pion.tech.pionbase.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import kotlin.collections.any

object AccessibilityServiceHelper {
    /**
     * Kiểm tra xem AccessibilityService có đang được bật không
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

        return enabledServices.any { service ->
            service.resolveInfo.serviceInfo.name == "pion.tech.pionbase.service.popupdetection.PopupDetectionService" &&
                service.resolveInfo.serviceInfo.packageName == context.packageName
        }
    }

    /**
     * Mở setting để bật AccessibilityService
     */
    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    /**
     * Lấy trạng thái chi tiết của AccessibilityService
     */
    fun getServiceStatus(context: Context): AccessibilityServiceStatus {
        val isEnabled = isAccessibilityServiceEnabled(context)
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val isAccessibilityEnabled = accessibilityManager.isEnabled

        return AccessibilityServiceStatus(
            isServiceEnabled = isEnabled,
            isAccessibilityEnabled = isAccessibilityEnabled,
            canDetectPopups = isEnabled && isAccessibilityEnabled,
        )
    }
}

data class AccessibilityServiceStatus(
    val isServiceEnabled: Boolean,
    val isAccessibilityEnabled: Boolean,
    val canDetectPopups: Boolean,
)
