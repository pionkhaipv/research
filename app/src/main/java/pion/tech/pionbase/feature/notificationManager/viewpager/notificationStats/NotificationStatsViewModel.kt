package pion.tech.pionbase.feature.notificationManager.viewpager.notificationStats

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.notification.NotificationUIModel
import pion.tech.pionbase.data.model.notification.toPresentation
import pion.tech.pionbase.data.repository.notificationRepository.NotificationRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class NotificationStatsViewModel
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) : BaseViewModel() {
        private val _recentNotificationsUiState =
            MutableStateFlow<UiState<List<NotificationUIModel>>>(UiState.None)
        val recentNotificationsUiState = _recentNotificationsUiState.asStateFlow()

        fun getRecentNotifications() {
            handleApiCall(
                stateFlow = _recentNotificationsUiState,
                apiCall = { notificationRepository.getRecentNotifications() },
                transform = { dtoList -> dtoList.map { it.toPresentation() } },
            )
        }
    }
