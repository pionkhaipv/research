package pion.tech.pionbase.feature.scanHiddenApp

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppDtoModel
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

        fun startScan() {
            handleApiCall(
                stateFlow = _hiddenAppsUiState,
                apiCall = { hiddenAppsRepository.getHiddenApps() },
                transform = { dtoList: List<HiddenAppDtoModel> -> 
                    dtoList.map { it.toPresentation() } 
                }
            )
        }

        fun resetScanState() {
            _hiddenAppsUiState.value = UiState.None
        }
    }
