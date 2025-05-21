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
            MutableStateFlow<GetAppCategoryUiState>(GetAppCategoryUiState.None)
        val getCategoryUiState = _getCategoryUiState.asStateFlow()

        private val _getTemplateUiState =
            MutableStateFlow<GetTemplateUiState>(GetTemplateUiState.None)
        val getTemplateUiState = _getTemplateUiState.asStateFlow()

        private fun fetchRemoteConfigData() {
            launchIO {
                remoteConfigRepository.fetchRemoteConfig().collect {
                    _remoteConfigDataStateFlow.value = it.toPresentation()
                }
            }
        }

        private fun getAppId() {
            launchIO {
                if (_getCategoryUiState.value is GetAppCategoryUiState.Standby ||
                    _getCategoryUiState.value is GetAppCategoryUiState.Success
                ) {
                    return@launchIO
                }

                _getCategoryUiState.value = GetAppCategoryUiState.Standby
                apiRepository.getAppCategory().collect {
                    it
                        .onSuccess { data ->
                            _getCategoryUiState.value =
                                GetAppCategoryUiState.Success(data.map { item -> item.toPresentation() })
                        }.onError {
                            _getCategoryUiState.value = GetAppCategoryUiState.Error
                        }
                }
            }
        }

        fun getTemplate(categoryId: String) {
            launchIO {
                if (_getTemplateUiState.value is GetTemplateUiState.Standby ||
                    _getTemplateUiState.value is GetTemplateUiState.Success
                ) {
                    return@launchIO
                }

                _getTemplateUiState.value = GetTemplateUiState.Standby
                apiRepository
                    .getTemplateData(categoryId)
                    .collect {
                        it
                            .onSuccess { data ->
                                _getTemplateUiState.value =
                                    GetTemplateUiState.Success(data.map { item -> item.toPresentation() })
                            }.onError {
                                _getTemplateUiState.value =
                                    GetTemplateUiState.Error
                            }
                    }
            }
        }

        fun getApiData() {
            if (_getCategoryUiState.value !is GetAppCategoryUiState.Success || _getTemplateUiState.value !is GetTemplateUiState.Success) {
                getAppId()
            }
        }
    }

sealed interface GetAppCategoryUiState {
    data object None : GetAppCategoryUiState

    data object Standby : GetAppCategoryUiState

    data class Success(
        val listAppCategory: List<AppCategoryUIModel>,
    ) : GetAppCategoryUiState

    data object Error : GetAppCategoryUiState
}

sealed interface GetTemplateUiState {
    data object None : GetTemplateUiState

    data object Standby : GetTemplateUiState

    data class Success(
        val listTemplate: List<TemplateUIModel>,
    ) : GetTemplateUiState

    data object Error : GetTemplateUiState
}
