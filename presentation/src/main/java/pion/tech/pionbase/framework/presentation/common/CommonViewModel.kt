package pion.tech.pionbase.framework.presentation.common

import com.piontech.domain.usecase.FetchRemoteConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import pion.tech.pionbase.framework.presentation.mapper.toPresentation
import pion.tech.pionbase.framework.presentation.model.RemoteConfigDataModel
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val fetchRemoteConfigUseCase: FetchRemoteConfigUseCase
) : BaseViewModel() {

    private val _remoteConfigDataStateFlow = MutableStateFlow<RemoteConfigDataModel?>(null)
    val remoteConfigDataStateFlow: StateFlow<RemoteConfigDataModel?> =
        _remoteConfigDataStateFlow.asStateFlow()

    init {
        fetchRemoteConfigData()
    }

    private fun fetchRemoteConfigData() {
        launchIO{
            fetchRemoteConfigUseCase.invoke()
                .catch {
                    it.printStackTrace()
                }
                .collect {
                        _remoteConfigDataStateFlow.value = it.toPresentation()
                }
        }
    }

}
