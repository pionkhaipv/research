package pion.tech.pionbase.service.popupdetection

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Analyzes accessibility nodes to detect ad content
 * Uses the Visitor pattern to traverse node tree
 */
class AdNodeAnalyzer {
    companion object {
        private const val TAG = "AdNodeAnalyzer"

        // Confidence thresholds
        private const val TEXT_AD_CONFIDENCE = 0.3f
        private const val CONTENT_AD_CONFIDENCE = 0.2f
        private const val VIEW_ID_AD_CONFIDENCE = 0.4f
        private const val CLASS_AD_CONFIDENCE = 0.3f
        private const val LAYOUT_AD_CONFIDENCE = 0.2f
    }

    fun analyzeForAds(node: AccessibilityNodeInfo): AdAnalysisResult {
        val result = AdAnalysisResult()

        try {
            // Analyze current node
            analyzeCurrentNode(node, result)

            // Analyze children recursively
            analyzeChildNodes(node, result)

            // Check layout characteristics
            if (hasAdLayoutCharacteristics(node)) {
                result.markAsAd("LAYOUT_AD", LAYOUT_AD_CONFIDENCE)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error analyzing node for ads", e)
        }

        return result
    }

    private fun analyzeCurrentNode(
        node: AccessibilityNodeInfo,
        result: AdAnalysisResult,
    ) {
        // Check text content
        node.text?.let { text ->
            if (AdPatternDetector.containsAdKeywords(text.toString())) {
                result.markAsAd("TEXT_AD", TEXT_AD_CONFIDENCE)
            }
        }

        // Check content description
        node.contentDescription?.let { description ->
            if (AdPatternDetector.containsAdKeywords(description.toString())) {
                result.markAsAd("CONTENT_AD", CONTENT_AD_CONFIDENCE)
            }
        }

        // Check view ID
        node.viewIdResourceName?.let { viewId ->
            if (AdPatternDetector.containsAdKeywords(viewId)) {
                result.markAsAd("VIEW_ID_AD", VIEW_ID_AD_CONFIDENCE)
            }
        }

        // Check class name
        node.className?.let { className ->
            if (AdPatternDetector.containsAdKeywords(className.toString())) {
                result.markAsAd("CLASS_AD", CLASS_AD_CONFIDENCE)
            }
        }
    }

    private fun analyzeChildNodes(
        node: AccessibilityNodeInfo,
        result: AdAnalysisResult,
    ) {
        for (i in 0 until node.childCount) {
            try {
                val childNode = node.getChild(i) ?: continue
                val childResult = analyzeForAds(childNode)

                if (childResult.isAd) {
                    result.mergeWith(childResult)
                }

                childNode.recycle()
            } catch (e: Exception) {
                Log.w(TAG, "Error analyzing child node", e)
            }
        }
    }

    private fun hasAdLayoutCharacteristics(node: AccessibilityNodeInfo): Boolean =
        findCloseButton(node) || findSkipButton(node) || findAdLabel(node)

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
        // Check current node
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

        // Check children
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

    fun isLikelyAdView(node: AccessibilityNodeInfo): Boolean {
        val className = node.className?.toString() ?: return false

        // Check class name for ad-related keywords
        val adViewClasses = listOf("AdView", "Banner", "Interstitial", "VideoAd", "NativeAd", "PopupWindow", "Dialog")

        if (adViewClasses.any { className.contains(it, ignoreCase = true) }) {
            return true
        }

        // Check view ID
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
}

/**
 * Result of ad analysis with confidence scoring
 */
data class AdAnalysisResult(
    var isAd: Boolean = false,
    var adType: String = "UNKNOWN",
    var confidence: Float = 0f,
) {
    fun markAsAd(
        type: String,
        confidenceBoost: Float,
    ) {
        isAd = true
        adType = type
        confidence += confidenceBoost
    }

    fun mergeWith(other: AdAnalysisResult) {
        if (other.isAd) {
            isAd = true
            if (other.confidence > confidence) {
                adType = other.adType
            }
            confidence = maxOf(confidence, other.confidence)
        }
    }
}
