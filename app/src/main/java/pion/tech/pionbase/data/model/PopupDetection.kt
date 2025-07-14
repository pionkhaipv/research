package pion.tech.pionbase.data.model

data class PopupDetection(
    val id: Long = 0,
    val appPackage: String,
    val appName: String,
    val popupType: String,
    val detectionTime: Long,
    val isBlocked: Boolean = false,
    val confidence: Float = 0f,
)

data class PopupStatistics(
    val totalDetections: Int,
    val blockedPopups: Int,
    val detectionsByApp: Map<String, Int>,
    val detectionsByType: Map<String, Int>,
    val todayDetections: Int,
    val weekDetections: Int,
)
