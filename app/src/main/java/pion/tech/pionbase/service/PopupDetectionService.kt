package pion.tech.pionbase.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pion.tech.pionbase.app.data.model.PopupDetectionEntity
import pion.tech.pionbase.app.data.roomDb.dao.PopupDetectionDAO
import javax.inject.Inject

@AndroidEntryPoint
class PopupDetectionService : AccessibilityService() {
    companion object {
        private const val TAG = "PopupDetectionService"
        const val ACTION_POPUP_DETECTED = "pion.tech.pionbase.POPUP_DETECTED"
        const val EXTRA_APP_PACKAGE = "app_package"
        const val EXTRA_APP_NAME = "app_name"
        const val EXTRA_POPUP_TYPE = "popup_type"
        const val EXTRA_DETECTION_TIME = "detection_time"

        // Thêm constants để throttling
        private const val ANALYSIS_THROTTLE_MS = 1000L // 1 giây
        private const val MIN_TIME_BETWEEN_SAME_PACKAGE_MS = 2000L // 2 giây
    }

    private val handler = Handler(Looper.getMainLooper())
    private val appPackageManager by lazy { applicationContext.packageManager }

    // Thêm tracking để tránh phân tích quá nhiều
    private var lastAnalysisTime = 0L
    private var lastAnalyzedPackage = ""
    private var lastAnalyzedPackageTime = 0L
    private val analyzedPackages = mutableSetOf<String>()

    // Danh sách từ khóa để phát hiện popup quảng cáo
    private val adKeywords =
        listOf(
            "quảng cáo",
            "advertisement",
            "ads",
            "ad",
            "promotion",
            "promo",
            "download",
            "install",
            "free",
            "win",
            "prize",
            "reward",
            "gift",
            "lucky",
            "congratulations",
            "claim",
            "offer",
            "deal",
            "sale",
            "discount",
            "limited time",
            "urgent",
            "act now",
            "click here",
            "video ad",
            "sponsored",
            "promoted",
            "banner",
        )

    @Inject
    lateinit var popupDetectionDAO: PopupDetectionDAO

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Popup Detection Service Connected")

        val info =
            AccessibilityServiceInfo().apply {
                eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_CLICKED or
                    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED

                feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
                flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS

                notificationTimeout = 100
            }

        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let { accessibilityEvent ->
            when (accessibilityEvent.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    handleWindowStateChanged(accessibilityEvent)
                }
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    handleWindowContentChanged(accessibilityEvent)
                }
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    handleViewClicked(accessibilityEvent)
                }
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                    handleNotificationChanged(accessibilityEvent)
                }
            }
        }
    }

    private fun handleWindowStateChanged(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        val className = event.className?.toString() ?: return

        Log.d(TAG, "Window state changed: $packageName, $className")

        // Kiểm tra xem có phải là popup, dialog, hoặc ad activity không
        if (isLikelyPopup(className) || isKnownAdActivity(className) || isKnownAdPackage(packageName)) {
            Log.d(TAG, "Detected potential ad/popup - analyzing: $packageName, $className")
            analyzeCurrentWindow(packageName)
        }
    }

    private fun handleWindowContentChanged(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return

        // Bỏ qua nếu là package của chính ứng dụng
        if (packageName == this.packageName) return

        // Bỏ qua một số package hệ thống không cần thiết
        if (isSystemPackage(packageName)) return

        // Chỉ phân tích nếu có className cho thấy có thể là popup/dialog
        val className = event.className?.toString()
        if (className != null && !isLikelyPopup(className) && !isLikelyAdRelated(className)) {
            return
        }

        // Đợi một chút để UI ổn định rồi mới phân tích
        handler.removeCallbacksAndMessages(packageName) // Hủy pending analysis cho package này
        handler.postDelayed({
            analyzeCurrentWindow(packageName)
        }, 500) // Tăng delay để tránh spam
    }

    private fun handleViewClicked(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return

        // Bỏ qua nếu là package của chính ứng dụng
        if (packageName == this.packageName) return

        // Kiểm tra nếu view được click có chứa n��i dung quảng cáo
        event.source?.let { nodeInfo ->
            try {
                if (isLikelyAdView(nodeInfo) && containsAdContent(nodeInfo)) {
                    val appName = getAppName(packageName)
                    reportPopupDetected(packageName, appName, "AD_CLICK")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error analyzing clicked view", e)
            }
        }
    }

    private fun handleNotificationChanged(event: AccessibilityEvent) {
        val textList = event.text ?: return
        val text = textList.joinToString(" ")
        val packageName = event.packageName?.toString() ?: return

        if (containsAdKeywords(text)) {
            val appName = getAppName(packageName)
            reportPopupDetected(packageName, appName, "NOTIFICATION_AD")
        }
    }

    private fun isLikelyPopup(className: String): Boolean {
        val popupIndicators =
            listOf(
                "Dialog",
                "AlertDialog",
                "PopupWindow",
                "Toast",
                "BottomSheet",
                "Modal",
                "Overlay",
                "FloatingWindow",
                "AdActivity", // Thêm AdActivity
                "InterstitialActivity", // Thêm các activity quảng cáo khác
                "RewardedActivity",
                "BannerActivity",
            )

        return popupIndicators.any { className.contains(it, ignoreCase = true) }
    }

    private fun isKnownAdActivity(className: String): Boolean {
        val adActivityPatterns =
            listOf(
                // Google Ads
                "com.google.android.gms.ads.AdActivity",
                "com.google.android.gms.ads.InterstitialActivity",
                // Facebook Ads
                "com.facebook.ads.InterstitialActivity",
                "com.facebook.ads.AudienceNetworkActivity",
                // Unity Ads
                "com.unity3d.ads.adunit.AdUnitActivity",
                "com.unity3d.services.ads.adunit.AdUnitActivity",
                // IronSource
                "com.ironsource.sdk.controller.ControllerActivity",
                "com.ironsource.sdk.controller.InterstitialActivity",
                // AppLovin
                "com.applovin.adview.AppLovinInterstitialActivity",
                "com.applovin.adview.AppLovinFullscreenActivity",
                // Vungle
                "com.vungle.publisher.VungleActivity",
                "com.vungle.warren.ui.VungleActivity",
                // Chartboost
                "com.chartboost.sdk.CBImpressionActivity",
                // ByteDance/TikTok Ads - THÊM MỚI
                "com.bytedance.sdk.openadsdk.activity.TTFullScreenExpressVideoActivity",
                "com.bytedance.sdk.openadsdk.activity.TTVideoLandingPageActivity",
                "com.bytedance.sdk.openadsdk.activity.TTFullScreenVideoActivity",
                "com.bytedance.sdk.openadsdk.activity.TTRewardVideoActivity",
                "com.bytedance.sdk.openadsdk.activity.TTInterstitialActivity",
                "com.bytedance.sdk.openadsdk.activity.TTAppOpenAdActivity",
                // AdMob
                "com.google.ads.mediation.admob.AdMobAdapter",
                // Mintegral
                "com.mbridge.msdk.activity.MBCommonActivity",
                "com.mbridge.msdk.reward.player.MBRewardVideoActivity",
                // StartApp
                "com.startapp.android.publish.ads.splash.SplashActivity",
                "com.startapp.android.publish.adsCommon.activities.OverlayActivity",
                // InMobi
                "com.inmobi.ads.rendering.InMobiAdActivity",
                // Tapjoy
                "com.tapjoy.TJAdUnitActivity",
                "com.tapjoy.TJContentActivity",
            )

        return adActivityPatterns.any { className.contains(it, ignoreCase = true) }
    }

    private fun isKnownAdPackage(packageName: String): Boolean {
        val knownAdPackages =
            setOf(
                // Google
                "com.google.android.gms.ads",
                "com.google.android.gms",
                // Facebook
                "com.facebook.ads",
                // Unity
                "com.unity3d.ads",
                // IronSource
                "com.ironsource",
                // AppLovin
                "com.applovin",
                // Vungle
                "com.vungle",
                // Chartboost
                "com.chartboost",
                // Tapjoy
                "com.tapjoy",
                // ByteDance/TikTok Ads - THÊM MỚI
                "com.bytedance.sdk.openadsdk",
                "com.bytedance.sdk",
                // Mintegral
                "com.mbridge.msdk",
                // StartApp
                "com.startapp.android",
                // InMobi
                "com.inmobi.ads",
                // AdColony
                "com.adcolony.sdk",
            )

        return knownAdPackages.any { packageName.contains(it) }
    }

    private fun analyzeCurrentWindow(
        packageName: String,
        className: String? = null,
    ) {
        val tag = "analyzeCurrentWindow"
        val currentTime = System.currentTimeMillis()

        // Throttling: Kiểm tra thời gian phân tích lần cuối
        if (currentTime - lastAnalysisTime < ANALYSIS_THROTTLE_MS) {
            Log.d(tag, "Throttling analysis for package: $packageName")
            return
        }

        // Kiểm tra thời gian tối thiểu giữa các lần phân tích cùng một package
        if (packageName == lastAnalyzedPackage &&
            currentTime - lastAnalyzedPackageTime < MIN_TIME_BETWEEN_SAME_PACKAGE_MS
        ) {
            Log.d(tag, "Skipping analysis for the same package: $packageName")
            return
        }

        try {
            val rootNode = rootInActiveWindow ?: return

            // Phân tích nội dung của cửa sổ hiện tại
            val analysisResult = analyzeNodeForAds(rootNode)

            if (analysisResult.isAd) {
                val appName = getAppName(packageName)
                Log.d(tag, "Popup ad detected in $appName ($packageName)")

                reportPopupDetected(
                    packageName = packageName,
                    appName = appName,
                    popupType = analysisResult.adType,
                )
            }
        } catch (e: Exception) {
            Log.e(tag, "Error analyzing current window", e)
        } finally {
            // Cập nhật thời gian phân tích lần cuối
            lastAnalysisTime = currentTime
            lastAnalyzedPackage = packageName
            lastAnalyzedPackageTime = currentTime
        }
    }

    private fun analyzeNodeForAds(node: AccessibilityNodeInfo): AdAnalysisResult {
        val result = AdAnalysisResult()

        // Kiểm tra text content
        node.text?.let { text ->
            if (containsAdKeywords(text.toString())) {
                result.isAd = true
                result.adType = "TEXT_AD"
                result.confidence += 0.3f
            }
        }

        // Kiểm tra content description
        node.contentDescription?.let { description ->
            if (containsAdKeywords(description.toString())) {
                result.isAd = true
                result.adType = "CONTENT_AD"
                result.confidence += 0.2f
            }
        }

        // Kiểm tra view ID
        node.viewIdResourceName?.let { viewId ->
            if (containsAdKeywords(viewId)) {
                result.isAd = true
                result.adType = "VIEW_ID_AD"
                result.confidence += 0.4f
            }
        }

        // Kiểm tra class name
        node.className?.let { className ->
            if (containsAdKeywords(className.toString())) {
                result.isAd = true
                result.adType = "CLASS_AD"
                result.confidence += 0.3f
            }
        }

        // Phân tích các node con
        for (i in 0 until node.childCount) {
            try {
                val childNode = node.getChild(i) ?: continue
                val childResult = analyzeNodeForAds(childNode)

                if (childResult.isAd) {
                    result.isAd = true
                    result.adType = childResult.adType
                    result.confidence = maxOf(result.confidence, childResult.confidence)
                }

                childNode.recycle()
            } catch (e: Exception) {
                Log.w(TAG, "Error analyzing child node", e)
            }
        }

        // Kiểm tra layout đ��c trung của quảng cáo
        if (hasAdLayoutCharacteristics(node)) {
            result.isAd = true
            result.adType = "LAYOUT_AD"
            result.confidence += 0.2f
        }

        return result
    }

    private fun isLikelyAdView(node: AccessibilityNodeInfo): Boolean {
        // Kiểm tra các đặc điểm của view có khả năng là quảng cáo
        val className = node.className?.toString() ?: return false

        // Kiểm tra class name có chứa từ khóa liên quan đến quảng cáo
        val adViewClasses =
            listOf(
                "AdView",
                "Banner",
                "Interstitial",
                "VideoAd",
                "NativeAd",
                "PopupWindow",
                "Dialog",
            )

        if (adViewClasses.any { className.contains(it, ignoreCase = true) }) {
            return true
        }

        // Kiểm tra view ID
        node.viewIdResourceName?.let { viewId ->
            if (viewId.contains("ad", ignoreCase = true) ||
                viewId.contains("banner", ignoreCase = true) ||
                viewId.contains("popup", ignoreCase = true)
            ) {
                return true
            }
        }

        return false
    }

    private fun containsAdContent(node: AccessibilityNodeInfo): Boolean = analyzeNodeForAds(node).isAd

    private fun containsAdKeywords(text: String): Boolean {
        val lowerText = text.lowercase()
        return adKeywords.any { keyword ->
            lowerText.contains(keyword.lowercase())
        }
    }

    private fun hasAdLayoutCharacteristics(node: AccessibilityNodeInfo): Boolean {
        // Kiểm tra các đặc điểm layout của quảng cáo
        val hasCloseButton = findCloseButton(node)
        val hasSkipButton = findSkipButton(node)
        val hasAdLabel = findAdLabel(node)

        return hasCloseButton || hasSkipButton || hasAdLabel
    }

    private fun findCloseButton(node: AccessibilityNodeInfo): Boolean {
        val closeTexts = listOf("×", "✕", "close", "đóng", "skip", "bỏ qua")

        return findNodeWithTexts(node, closeTexts)
    }

    private fun findSkipButton(node: AccessibilityNodeInfo): Boolean {
        val skipTexts = listOf("skip", "bỏ qua", "skip ad", "bỏ qua quảng cáo")

        return findNodeWithTexts(node, skipTexts)
    }

    private fun findAdLabel(node: AccessibilityNodeInfo): Boolean {
        val adLabels = listOf("ad", "quảng cáo", "sponsored", "promotion")

        return findNodeWithTexts(node, adLabels)
    }

    private fun findNodeWithTexts(
        node: AccessibilityNodeInfo,
        texts: List<String>,
    ): Boolean {
        // Kiểm tra node hi���n tại
        node.text?.let { nodeText ->
            if (texts.any { nodeText.toString().contains(it, ignoreCase = true) }) {
                return true
            }
        }

        node.contentDescription?.let { description ->
            if (texts.any { description.toString().contains(it, ignoreCase = true) }) {
                return true
            }
        }

        // Kiểm tra các node con
        for (i in 0 until node.childCount) {
            try {
                val childNode = node.getChild(i) ?: continue
                if (findNodeWithTexts(childNode, texts)) {
                    childNode.recycle()
                    return true
                }
                childNode.recycle()
            } catch (e: Exception) {
                Log.w(TAG, "Error checking child node", e)
            }
        }

        return false
    }

    private fun isSystemPackage(packageName: String): Boolean {
        val systemPackages =
            setOf(
                "com.android.systemui",
                "android",
                "com.android.launcher",
                "com.android.launcher3",
                "com.android.settings",
                "com.google.android.inputmethod.latin",
                "com.android.inputmethod.latin",
            )
        return systemPackages.any { packageName.startsWith(it) }
    }

    private fun isLikelyAdRelated(className: String): Boolean {
        val adRelatedClasses =
            listOf(
                "AdView",
                "Banner",
                "Advertisement",
                "Popup",
                "Modal",
                "Interstitial",
                "Overlay",
            )
        return adRelatedClasses.any { className.contains(it, ignoreCase = true) }
    }

    private fun getAppName(packageName: String): String =
        try {
            val appInfo = appPackageManager.getApplicationInfo(packageName, 0)
            appPackageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }

    private fun reportPopupDetected(
        packageName: String,
        appName: String,
        popupType: String,
    ) {
        Log.i(TAG, "Popup detected - App: $appName, Package: $packageName, Type: $popupType")

        val currentTime = System.currentTimeMillis()

        // Lưu vào Room Database
        serviceScope.launch {
            try {
                val popupDetection =
                    PopupDetectionEntity(
                        appPackage = packageName,
                        appName = appName,
                        popupType = popupType,
                        detectionTime = currentTime,
                    )

                val insertedId = popupDetectionDAO.insert(popupDetection)
                Log.d(TAG, "Popup detection saved to database with ID: $insertedId")

                // Xóa các bản ghi cũ hơn 30 ngày để tránh database quá lớn
                val thirtyDaysAgo = currentTime - (30 * 24 * 60 * 60 * 1000L)
                val deletedCount = popupDetectionDAO.deleteOldRecords(thirtyDaysAgo)
                if (deletedCount > 0) {
                    Log.d(TAG, "Deleted $deletedCount old popup detection records")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving popup detection to database", e)
            }
        }

        // Hiển thị notification cho user
        PopupNotificationHelper.showPopupDetectedNotification(
            context = this,
            appName = appName,
            popupType = popupType,
        )

        // Gửi broadcast để thông báo đã phát hiện popup
        val intent =
            Intent(ACTION_POPUP_DETECTED).apply {
                putExtra(EXTRA_APP_PACKAGE, packageName)
                putExtra(EXTRA_APP_NAME, appName)
                putExtra(EXTRA_POPUP_TYPE, popupType)
                putExtra(EXTRA_DETECTION_TIME, currentTime)
            }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onInterrupt() {
        Log.d(TAG, "Popup Detection Service Interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Popup Detection Service Destroyed")
    }

    private data class AdAnalysisResult(
        var isAd: Boolean = false,
        var adType: String = "UNKNOWN",
        var confidence: Float = 0f,
    )
}
