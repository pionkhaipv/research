package pion.tech.pionbase.app

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.appCategory.AppCategoryUIModel
import pion.tech.pionbase.data.model.appCategory.toPresentation
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.data.model.template.toPresentation
import pion.tech.pionbase.data.repository.apiRepository.ApiRepository
import pion.tech.pionbase.data.repository.remoteConfig.RemoteConfigRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class CommonViewModel
    @Inject
    constructor(
        private val remoteConfigRepository: RemoteConfigRepository,
        private val apiRepository: ApiRepository,
    ) : BaseViewModel() {
        // Cached remote config data for easy access across the app
        val cachedRemoteConfig = remoteConfigRepository.getCachedRemoteConfig()

        init {
            // Fetch remote config data to populate the cache
            launchIO {
                remoteConfigRepository
                    .fetchRemoteConfig()
                    .collect { /* Data is automatically cached in repository */ }
            }
        }

        private val _getCategoryUiState =
            MutableStateFlow<UiState<List<AppCategoryUIModel>>>(UiState.None)
        val getCategoryUiState = _getCategoryUiState.asStateFlow()

        private val _getTemplateUiState =
            MutableStateFlow<UiState<List<TemplateUIModel>>>(UiState.None)
        val getTemplateUiState = _getTemplateUiState.asStateFlow()

        private fun getAppId() {
            handleApiCall(
                stateFlow = _getCategoryUiState,
                apiCall = { apiRepository.getAppCategory() },
                transform = { data -> data.map { item -> item.toPresentation() } },
            )
        }

        fun getTemplate(categoryId: String) {
            handleApiCall(
                stateFlow = _getTemplateUiState,
                apiCall = { apiRepository.getTemplateData(categoryId) },
                transform = { data -> data.map { item -> item.toPresentation() } },
            )
        }

        fun getApiData() {
            if (_getCategoryUiState.value !is UiState.Success || _getTemplateUiState.value !is UiState.Success) {
                getAppId()
            }
        }
    }
