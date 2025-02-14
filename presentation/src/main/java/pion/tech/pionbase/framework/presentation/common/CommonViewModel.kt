package pion.tech.pionbase.framework.presentation.common

import com.piontech.domain.usecase.FetchRemoteConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.framework.presentation.mapper.toPresentation
import pion.tech.pionbase.framework.presentation.model.RemoteConfigDataModel
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val fetchRemoteConfigUseCase: FetchRemoteConfigUseCase
) : BaseViewModel() {

    private val _remoteConfigDataStateFlow = MutableStateFlow<RemoteConfigDataStatus>(RemoteConfigDataStatus.None)
    val remoteConfigDataStateFlow: StateFlow<RemoteConfigDataStatus> = _remoteConfigDataStateFlow.asStateFlow()

    init {
        fetchRemoteConfigData()
    }

    private fun fetchRemoteConfigData() {
        launchIO(
            onError = {
                _remoteConfigDataStateFlow.value = RemoteConfigDataStatus.Error
            }

        ) {
            _remoteConfigDataStateFlow.value = RemoteConfigDataStatus.Standby
            val data = fetchRemoteConfigUseCase.invoke()
            _remoteConfigDataStateFlow.value = RemoteConfigDataStatus.Success(data.toPresentation())
        }
    }

}

sealed class RemoteConfigDataStatus {
    data object None : RemoteConfigDataStatus()
    data object Standby : RemoteConfigDataStatus()
    data class Success(val remoteConfigDataModel: RemoteConfigDataModel) : RemoteConfigDataStatus()
    data object Error : RemoteConfigDataStatus()
}