package pion.tech.pionbase.feature.home

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.data.model.installedApp.toPresentation
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleLocalDataCall
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

        fun plusValue() {
            _countValue.value += 1
        }

        suspend fun getIsPremiumValue(): Flow<Boolean> = dataStoreRepository.getIsPremium()

        fun getInstalledApps() {
            handleLocalDataCall(
                stateFlow = _installedAppsUiState,
                dataCall = {
                    installedAppsRepository.getInstalledApps().map { it.toPresentation() }
                },
            )
        }
    }
