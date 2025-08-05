package pion.tech.pionbase.feature.scanHiddenApp

import androidx.lifecycle.viewModelScope
import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppDtoModel
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import pion.tech.pionbase.data.model.hiddenApp.toPresentation
import pion.tech.pionbase.data.repository.hiddenAppRepository.HiddenAppsRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class ScanHiddenAppViewModel
    @Inject
    constructor(
        private val hiddenAppsRepository: HiddenAppsRepository,
    ) : BaseViewModel() {
        private val _hiddenAppsUiState =
            MutableStateFlow<UiState<List<HiddenAppUIModel>>>(UiState.None)
        val hiddenAppsUiState = _hiddenAppsUiState.asStateFlow()

        private val _scanTimeSeconds = MutableStateFlow(0)
        val scanTimeSeconds = _scanTimeSeconds.asStateFlow()

        private var timerJob: Job? = null

        fun startScan() {
            // Reset timer
            _scanTimeSeconds.value = 0

            // Start timer
            startTimer()

            handleApiCall(
                stateFlow = _hiddenAppsUiState,
                apiCall = { hiddenAppsRepository.getHiddenApps() },
                transform = { dtoList: List<HiddenAppDtoModel> ->
                    dtoList.map { it.toPresentation() }
                },
            )
        }

        private fun startTimer() {
            // Cancel any existing timer job
            timerJob?.cancel()

            // Start a new timer job
            timerJob =
                launchIO {
                    _scanTimeSeconds.value = 0
                    while (isActive) {
                        delay(1000) // Update every second
                        _scanTimeSeconds.value += 1
                    }
                }
        }

        fun resetScanState() {
            _hiddenAppsUiState.value = UiState.None
            stopTimer()
        }

        private fun stopTimer() {
            timerJob?.cancel()
            timerJob = null
            _scanTimeSeconds.value = 0
        }

        override fun onCleared() {
            super.onCleared()
            stopTimer()
        }
    }
