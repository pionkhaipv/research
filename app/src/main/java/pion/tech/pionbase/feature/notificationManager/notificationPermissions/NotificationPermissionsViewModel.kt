package pion.tech.pionbase.feature.notificationManager.notificationPermissions

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionUIModel
import pion.tech.pionbase.data.model.notification.toPresentation
import pion.tech.pionbase.data.repository.notificationRepository.NotificationRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import pion.tech.pionbase.util.onSuccess
import javax.inject.Inject

@HiltViewModel
class NotificationPermissionsViewModel
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) : BaseViewModel() {

        private val _appPermissionsUiState =
            MutableStateFlow<UiState<List<AppNotificationPermissionUIModel>>>(UiState.None)
        val appPermissionsUiState = _appPermissionsUiState.asStateFlow()

        private val _togglePermissionUiState =
            MutableStateFlow<UiState<Boolean>>(UiState.None)
        val togglePermissionUiState = _togglePermissionUiState.asStateFlow()

        fun getAppsWithNotificationPermissions() {
            handleApiCall(
                stateFlow = _appPermissionsUiState,
                apiCall = { notificationRepository.getAppsWithNotificationPermissions() },
                transform = { dtoList -> dtoList.map { it.toPresentation() } }
            )
        }

        fun toggleAppNotificationPermission(packageName: String, enabled: Boolean) {
            launchIO {
                notificationRepository.toggleNotificationPermission(packageName, enabled).collect { result ->
                    result.onSuccess {
                        // Refresh the app permissions list after toggling
                        getAppsWithNotificationPermissions()
                    }
                }
            }
        }
    }