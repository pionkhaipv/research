package pion.tech.pionbase.feature.popup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pion.tech.pionbase.app.data.roomDb.dao.PopupDetectionDAO
import javax.inject.Inject

@HiltViewModel
class PopupStatisticsViewModel
    @Inject
    constructor(
        private val popupDetectionDAO: PopupDetectionDAO,
    ) : ViewModel() {
        // Flow để lấy tất cả popup detections
        val detections =
            popupDetectionDAO
                .getAllPopupDetections()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        // Flow để lấy tổng số popup
        val totalCount =
            popupDetectionDAO
                .getTotalPopupCount()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = 0,
                )

        // Flow để lấy thống kê popup theo loại
        val popupTypeStatistics =
            popupDetectionDAO
                .getPopupTypeStatistics()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        // Flow để lấy danh sách app có popup - sử dụng AppInfo
        val uniqueApps =
            popupDetectionDAO
                .getUniqueAppsWithPopups()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        // Computed properties từ detections flow
        val todayDetections =
            detections
                .map { detectionList ->
                    val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
                    detectionList.count { it.detectionTime >= oneDayAgo }
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = 0,
                )

        val topAppWithMostPopups =
            detections
                .map { detectionList ->
                    detectionList
                        .groupBy { it.appName }
                        .maxByOrNull { it.value.size }
                        ?.let { it.key to it.value.size }
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null,
                )

        // Loading state
        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        // Error state
        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        /**
         * Xóa tất cả popup detections
         */
        fun clearAllDetections() {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    popupDetectionDAO.deleteAll()
                    _errorMessage.value = null
                } catch (e: Exception) {
                    _errorMessage.value = "Không thể xóa dữ liệu: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        /**
         * Xóa popup detections của một app cụ thể
         */
        fun clearDetectionsByPackage(packageName: String) {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    popupDetectionDAO.deleteByPackage(packageName)
                    _errorMessage.value = null
                } catch (e: Exception) {
                    _errorMessage.value = "Không thể xóa dữ liệu của app: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        /**
         * Xóa các bản ghi cũ (trước một thời điểm nhất định)
         */
        fun clearOldRecords(beforeTime: Long) {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    val deletedCount = popupDetectionDAO.deleteOldRecords(beforeTime)
                    _errorMessage.value = null
                } catch (e: Exception) {
                    _errorMessage.value = "Không thể xóa dữ liệu cũ: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        /**
         * Lấy popup detections trong khoảng thời gian
         */
        fun getDetectionsByTimeRange(
            startTime: Long,
            endTime: Long,
        ) = popupDetectionDAO.getPopupDetectionsByTimeRange(startTime, endTime)

        /**
         * Lấy popup detections của một app cụ thể
         */
        fun getDetectionsByPackage(packageName: String) = popupDetectionDAO.getPopupDetectionsByPackage(packageName)

        /**
         * Clear error message
         */
        fun clearError() {
            _errorMessage.value = null
        }
    }
