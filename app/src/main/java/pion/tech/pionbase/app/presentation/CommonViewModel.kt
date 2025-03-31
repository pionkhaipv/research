package pion.tech.pionbase.app.presentation

import pion.tech.pionbase.home.domain.usecase.AppCategoryUseCase
import pion.tech.pionbase.app.domain.usecase.FetchRemoteConfigUseCase
import pion.tech.pionbase.home.domain.usecase.TemplateUseCase
import pion.tech.pionbase.core.presentation.util.onError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import pion.tech.pionbase.app.presentation.mapper.toPresentation
import pion.tech.pionbase.core.presentation.common.BaseViewModel
import pion.tech.pionbase.core.presentation.common.launchIO
import pion.tech.pionbase.core.presentation.util.onSuccess
import pion.tech.pionbase.home.presetation.mapper.toPresentation
import pion.tech.pionbase.model.AppCategoryUIModel
import pion.tech.pionbase.model.RemoteConfigDataModel
import pion.tech.pionbase.model.TemplateUIModel
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val fetchRemoteConfigUseCase: FetchRemoteConfigUseCase,
    private val appCategoryUseCase: AppCategoryUseCase,
    private val templateUseCase: TemplateUseCase
) : BaseViewModel() {

    private val _remoteConfigDataStateFlow = MutableStateFlow<RemoteConfigDataModel?>(null)
    val remoteConfigDataStateFlow: StateFlow<RemoteConfigDataModel?> =
        _remoteConfigDataStateFlow.asStateFlow()

    init {
        fetchRemoteConfigData()
    }

    private val _getCategoryUiState =
        MutableStateFlow<GetAppCategoryUiState>(GetAppCategoryUiState.None)
    val getCategoryUiState: StateFlow<GetAppCategoryUiState> get() = _getCategoryUiState.asStateFlow()

    private val _getTemplateUiState =
        MutableStateFlow<GetTemplateUiState>(GetTemplateUiState.None)
    val getTemplateUiState: StateFlow<GetTemplateUiState> get() = _getTemplateUiState.asStateFlow()

    private fun fetchRemoteConfigData() {
        launchIO {
            fetchRemoteConfigUseCase.invoke()
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    _remoteConfigDataStateFlow.value = it.toPresentation()
                }
        }
    }

    private fun getAppId() {
        launchIO {
            if (_getCategoryUiState.value is GetAppCategoryUiState.Standby || _getCategoryUiState.value is GetAppCategoryUiState.Success) return@launchIO

            _getCategoryUiState.value = GetAppCategoryUiState.Standby
            appCategoryUseCase.invoke().collect {
                it.onSuccess { data ->
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
            if (_getTemplateUiState.value is GetTemplateUiState.Standby || _getTemplateUiState.value is GetTemplateUiState.Success) return@launchIO

            _getTemplateUiState.value = GetTemplateUiState.Standby
            templateUseCase.invoke(categoryId)
                .collect {
                    it.onSuccess { data ->
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
    data class Success(val listAppCategory: List<AppCategoryUIModel>) : GetAppCategoryUiState
    data object Error : GetAppCategoryUiState
}

sealed interface GetTemplateUiState {
    data object None : GetTemplateUiState
    data object Standby : GetTemplateUiState
    data class Success(val listTemplate: List<TemplateUIModel>) : GetTemplateUiState
    data object Error : GetTemplateUiState
}
