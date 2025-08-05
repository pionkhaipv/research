package pion.tech.pionbase.feature.notificationManager.notificationBlock

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import com.piontech.core.base.launchMain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionUIModel
import pion.tech.pionbase.data.model.notification.toPresentation
import pion.tech.pionbase.data.repository.notificationRepository.NotificationRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import pion.tech.pionbase.util.onError
import pion.tech.pionbase.util.onSuccess
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationBlockViewModel
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) : BaseViewModel() {
        private val _appNotificationUiState =
            MutableStateFlow<UiState<List<AppNotificationPermissionUIModel>>>(UiState.None)
        val appNotificationUiState = _appNotificationUiState.asStateFlow()

        private val _searchQuery = MutableStateFlow("")
        val searchQuery = _searchQuery.asStateFlow()

        fun updateSearchQuery(query: String) {
            _searchQuery.value = query
        }

        fun getAppsWithNotification() {
            handleApiCall(
                stateFlow = _appNotificationUiState,
                apiCall = { notificationRepository.getAppsWithNotificationPermissions() },
                transform = { dtoList -> dtoList.map { it.toPresentation() } },
            )
        }

        val appFilteredList =
            combine(
                _appNotificationUiState,
                searchQuery.debounce(300),
            ) { appNotificationUiState, searchQuery ->
                when (appNotificationUiState) {
                    is UiState.None -> UiState.None
                    is UiState.Loading -> UiState.Loading
                    is UiState.Error -> appNotificationUiState
                    is UiState.Success -> {
                        val apps = appNotificationUiState.data
                        if (searchQuery.isNotBlank()) {
                            val filteredApps =
                                apps.filter { it.appName.contains(searchQuery, ignoreCase = true) }
                            UiState.Success(filteredApps)
                        } else {
                            UiState.Success(apps)
                        }
                    }
                }
            }

        fun resetAppNotificationUiState() {
            _appNotificationUiState.value = UiState.None
        }

        fun toggleAppNotificationPermission(
            packageName: String,
            enabled: Boolean,
            onError: (Throwable) -> Unit,
        ) {
            val tag = "toggleNotification"
            launchIO {
                val currentState = _appNotificationUiState.value
                if (currentState is UiState.Success) {
                    val currentApps = currentState.data
                    val updatedApps =
                        currentApps.map { app ->
                            if (app.packageName == packageName) {
                                app.copy(isNotificationEnabled = enabled)
                            } else {
                                app
                            }
                        }
                    _appNotificationUiState.value = UiState.Success(updatedApps)
                }
                notificationRepository
                    .toggleNotificationPermission(packageName, enabled)
                    .collect { result ->
                        result
                            .onSuccess {
                                Timber.tag(tag).d("toggle success")
                            }.onError {
                                Timber.tag(tag).d("toggle error: ${it.message}")
                                resetAppNotificationUiState()
                                getAppsWithNotification()
                                launchMain {
                                    onError(it)
                                }
                            }
                    }
            }
        }
    }
