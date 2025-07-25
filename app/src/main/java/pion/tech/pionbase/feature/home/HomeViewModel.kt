package pion.tech.pionbase.feature.home

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.data.model.installedApp.InstalledAppDtoModel
import pion.tech.pionbase.data.model.installedApp.toPresentation
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleLocalDataCall
import pion.tech.pionbase.util.handleApiCall
import pion.tech.pionbase.util.Result
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
        private val installedAppsRepository: InstalledAppsRepository,
    ) : BaseViewModel() {
        private val _countValue = MutableStateFlow(0)
        val countValue: StateFlow<Int> = _countValue

        private val _installedAppsUiState =
            MutableStateFlow<UiState<List<InstalledAppUIModel>>>(UiState.None)
        val installedAppsUiState = _installedAppsUiState.asStateFlow()

        private val _waitingForNotificationPermissions = MutableStateFlow(false)
        val waitingForNotificationPermissions = _waitingForNotificationPermissions.asStateFlow()

        fun plusValue() {
            _countValue.value += 1
        }

        suspend fun getIsPremiumValue(): Flow<Result<Boolean>> = dataStoreRepository.getIsPremium()

        fun getInstalledApps() {
            handleApiCall(
                stateFlow = _installedAppsUiState,
                apiCall = { installedAppsRepository.getInstalledApps() },
                transform = { dtoList: List<InstalledAppDtoModel> -> 
                    dtoList.map { it.toPresentation() } 
                }
            )
        }

        fun setWaitingForNotificationPermissions(waiting: Boolean) {
            _waitingForNotificationPermissions.value = waiting
        }

        fun isWaitingForNotificationPermissions(): Boolean {
            return _waitingForNotificationPermissions.value
        }
    }
