package pion.tech.pionbase.service.popupdetection

/**
 * Utility class for detecting ad-related patterns in class names, package names, and activities
 * Follows the Strategy pattern for different detection types
 */
object AdPatternDetector {
    private val POPUP_PATTERNS =
        setOf(
            // Standard Android UI
            "Dialog",
            "AlertDialog",
            "PopupWindow",
            "PopupMenu",
            "Toast",
            "Snackbar",
            // Material Design
            "BottomSheet",
            "BottomSheetDialog",
            "ModalBottomSheet",
            "MaterialAlertDialog",
            // Layout containers
            "Modal",
            "Overlay",
            "FloatingWindow",
            "FloatingActionButton",
            "Popup",
            // Ad-specific
            "AdActivity",
            "AdDialog",
            "InterstitialActivity",
            "InterstitialDialog",
            "RewardedActivity",
            "RewardedDialog",
            "BannerActivity",
            "VideoAdActivity",
            "FullscreenAdActivity",
            "NativeAdActivity",
            "AppOpenAdActivity",
            "SplashAdActivity",
            // WebView containers
            "WebView",
            "WebViewActivity",
            "WebViewDialog",
            "BrowserActivity",
            // Game-specific
            "GameAdActivity",
            "AdBreakActivity",
            "PauseAdActivity",
            // Custom containers
            "CustomDialog",
            "CustomPopup",
            "OverlayActivity",
            "TransparentActivity",
            // Loading screens
            "LoadingActivity",
            "SplashActivity",
            "LaunchActivity",
            "StartupActivity",
            // Survey/Rating
            "SurveyDialog",
            "PromotionDialog",
            "OfferDialog",
            "RatingDialog",
            "FeedbackDialog",
            // Additional common patterns
            "AdView",
            "AdContainer",
            "AdFrame",
            "AdLayout",
            "PopupView",
            "OverlayView",
            "ModalView",
            "InterstitialView",
            "BannerView",
            "VideoView",
            "WebDialog",
            "CustomView",
            "FullScreenView",
        )

    private val AD_ACTIVITY_PATTERNS =
        setOf(
            // Google Ads
            "com.google.android.gms.ads.AdActivity",
            "com.google.android.gms.ads.InterstitialActivity",
            "com.google.android.gms.ads.FullScreenContentActivity",
            "com.google.android.gms.ads.rewarded.RewardedAdActivity",
            "com.google.android.gms.ads.appopen.AppOpenAdActivity",
            // Meta/Facebook
            "com.facebook.ads.InterstitialActivity",
            "com.facebook.ads.AudienceNetworkActivity",
            "com.facebook.ads.RewardedVideoAdActivity",
            // Unity Ads
            "com.unity3d.ads.adunit.AdUnitActivity",
            "com.unity3d.services.ads.adunit.AdUnitActivity",
            "com.unity3d.ads.adunit.VideoPlayerActivity",
            // IronSource
            "com.ironsource.sdk.controller.ControllerActivity",
            "com.ironsource.sdk.controller.InterstitialActivity",
            "com.ironsource.mediationsdk.adunit.adapter.utility.AdapterBaseActivity",
            // AppLovin
            "com.applovin.adview.AppLovinInterstitialActivity",
            "com.applovin.mediation.ads.MaxAdActivity",
            "com.applovin.impl.adview.activity.FullscreenAdActivity",
            // ByteDance/Pangle
            "com.bytedance.sdk.openadsdk.activity.TTFullScreenVideoActivity",
            "com.bytedance.sdk.openadsdk.activity.TTRewardVideoActivity",
            "com.bytedance.sdk.openadsdk.activity.TTInterstitialActivity",
            // Other major networks
            "com.vungle.publisher.VungleActivity",
            "com.chartboost.sdk.CBImpressionActivity",
            "com.mbridge.msdk.activity.MBCommonActivity",
            "com.startapp.android.publish.adsCommon.activities.OverlayActivity",
            "com.inmobi.ads.rendering.InMobiAdActivity",
            "com.tapjoy.TJAdUnitActivity",
            "com.adcolony.sdk.AdColonyInterstitialActivity",
            // Generic patterns
            "AdActivity",
            "InterstitialActivity",
            "RewardedActivity",
            "VideoAdActivity",
        )

    private val AD_PACKAGE_PATTERNS =
        setOf(
            // Google
            "com.google.android.gms.ads",
            "com.google.ads.mediation",
            // Meta/Facebook
            "com.facebook.ads",
            "com.facebook.audience",
            // Major ad networks
            "com.unity3d.ads",
            "com.ironsource",
            "com.applovin",
            "com.vungle",
            "com.chartboost",
            "com.tapjoy",
            "com.bytedance.sdk.openadsdk",
            "com.mbridge.msdk",
            "com.startapp.android",
            "com.inmobi.ads",
            "com.adcolony.sdk",
            "com.my.target",
            "com.yandex.mobile.ads",
            "com.amazon.device.ads",
            "com.fyber.inneractive.sdk",
            "com.smaato.sdk",
            "com.pubmatic.sdk",
            "com.criteo.publisher",
            "com.kidoz.sdk",
            // Generic patterns
            "ads",
            "advertisement",
            "advertising",
            "mediation",
            "monetization",
        )

    private val AD_KEYWORDS =
        setOf(
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
            "app open",
            "splash",
            "launch",
            "loading",
            "continue",
            "continue to app",
        )

    fun isLikelyPopup(className: String): Boolean = POPUP_PATTERNS.any { className.contains(it, ignoreCase = true) }

    fun isKnownAdActivity(className: String): Boolean = AD_ACTIVITY_PATTERNS.any { className.contains(it, ignoreCase = true) }

    fun isKnownAdPackage(packageName: String): Boolean = AD_PACKAGE_PATTERNS.any { packageName.contains(it) }

    fun containsAdKeywords(text: String): Boolean {
        val lowerText = text.lowercase()
        return AD_KEYWORDS.any { lowerText.contains(it) }
    }

    fun isSystemPackage(packageName: String): Boolean {
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

    fun isLikelyAdRelated(className: String): Boolean {
        val adRelatedClasses = setOf("AdView", "Banner", "Advertisement", "Popup", "Modal", "Interstitial", "Overlay")
        return adRelatedClasses.any { className.contains(it, ignoreCase = true) }
    }
}
