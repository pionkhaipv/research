package pion.tech.pionbase.feature.scanConcernApp

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.concernApp.ConcernAppUIModel
import pion.tech.pionbase.data.model.concernApp.ConcernAppDtoModel
import pion.tech.pionbase.data.model.concernApp.toPresentation
import pion.tech.pionbase.data.repository.concernAppRepository.ConcernAppsRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class ScanConcernAppViewModel
    @Inject
    constructor(
        private val concernAppsRepository: ConcernAppsRepository,
    ) : BaseViewModel() {

        private val _concernAppsUiState =
            MutableStateFlow<UiState<List<ConcernAppUIModel>>>(UiState.None)
        val concernAppsUiState = _concernAppsUiState.asStateFlow()

        fun startScan() {
            handleApiCall(
                stateFlow = _concernAppsUiState,
                apiCall = { concernAppsRepository.getConcernApps() },
                transform = { dtoList: List<ConcernAppDtoModel> -> 
                    dtoList.map { it.toPresentation() } 
                }
            )
        }

        fun resetScanState() {
            _concernAppsUiState.value = UiState.None
        }
    }