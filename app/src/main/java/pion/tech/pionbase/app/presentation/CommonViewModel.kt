package pion.tech.pionbase.app.presentation

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.app.domain.repository.RemoteConfigRepository
import pion.tech.pionbase.app.presentation.model.RemoteConfigUIModel
import pion.tech.pionbase.app.presentation.model.toPresentation
import pion.tech.pionbase.feature.home.domain.repository.ApiRepository
import pion.tech.pionbase.feature.home.presetation.model.AppCategoryUIModel
import pion.tech.pionbase.feature.home.presetation.model.TemplateUIModel
import pion.tech.pionbase.feature.home.presetation.model.toPresentation
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import pion.tech.pionbase.util.onError
import pion.tech.pionbase.util.onSuccess
import javax.inject.Inject

@HiltViewModel
class CommonViewModel
    @Inject
    constructor(
        private val remoteConfigRepository: RemoteConfigRepository,
        private val apiRepository: ApiRepository,
    ) : BaseViewModel() {
        private val _remoteConfigDataStateFlow = MutableStateFlow<RemoteConfigUIModel?>(null)
        val remoteConfigDataStateFlow = _remoteConfigDataStateFlow.asStateFlow()

        init {
            fetchRemoteConfigData()
        }

        private val _getCategoryUiState =
            MutableStateFlow<UiState<List<AppCategoryUIModel>>>(UiState.None)
        val getCategoryUiState = _getCategoryUiState.asStateFlow()

        private val _getTemplateUiState =
            MutableStateFlow<UiState<List<TemplateUIModel>>>(UiState.None)
        val getTemplateUiState = _getTemplateUiState.asStateFlow()

        private fun fetchRemoteConfigData() {
            launchIO {
                remoteConfigRepository.fetchRemoteConfig().collect {
                    _remoteConfigDataStateFlow.value = it.toPresentation()
                }
            }
        }

        private fun getAppId() {
            handleApiCall(
                stateFlow = _getCategoryUiState,
                apiCall = { apiRepository.getAppCategory() },
                transform = { data -> data.map { item -> item.toPresentation() } }
            )
        }

        fun getTemplate(categoryId: String) {
            handleApiCall(
                stateFlow = _getTemplateUiState,
                apiCall = { apiRepository.getTemplateData(categoryId) },
                transform = { data -> data.map { item -> item.toPresentation() } }
            )
        }

        fun getApiData() {
            if (_getCategoryUiState.value !is UiState.Success || _getTemplateUiState.value !is UiState.Success) {
                getAppId()
            }
        }
    }
