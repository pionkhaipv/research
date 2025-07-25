package pion.tech.pionbase.feature.runningapps

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.runningApp.RunningAppUIModel
import pion.tech.pionbase.data.model.runningApp.toPresentation
import pion.tech.pionbase.data.repository.runningAppsRepository.RunningAppsRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class RunningAppsViewModel
    @Inject
    constructor(
        private val runningAppsRepository: RunningAppsRepository,
    ) : BaseViewModel() {
        private val _runningAppsUiState =
            MutableStateFlow<UiState<List<RunningAppUIModel>>>(UiState.None)
        val runningAppsUiState = _runningAppsUiState.asStateFlow()

        init {
            getRunningApps()
        }

        fun getRunningApps() {
            handleApiCall(
                stateFlow = _runningAppsUiState,
                apiCall = { runningAppsRepository.getRunningApps() },
                transform = { dtoList -> dtoList.map { it.toPresentation() } },
            )
        }

        fun refreshApps() {
            handleApiCall(
                stateFlow = _runningAppsUiState,
                apiCall = { runningAppsRepository.refreshRunningApps() },
                transform = { dtoList -> dtoList.map { it.toPresentation() } },
            )
        }
    }
