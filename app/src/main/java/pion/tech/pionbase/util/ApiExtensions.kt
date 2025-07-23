package pion.tech.pionbase.util

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Extension functions to reduce boilerplate code for API handling
 */

sealed interface ApiUiState<out T> {
    data object None : ApiUiState<Nothing>

    data object Standby : ApiUiState<Nothing>

    data class Success<T>(
        val data: T,
    ) : ApiUiState<T>

    data object Error : ApiUiState<Nothing>
}

/**
 * Extension function to handle API calls with automatic state management
 */
inline fun <T, R> BaseViewModel.handleApiCall(
    stateFlow: MutableStateFlow<ApiUiState<T>>,
    crossinline apiCall: suspend () -> Flow<Result<R>>,
    crossinline transform: (R) -> T,
    skipIfInProgress: Boolean = true,
) {
    launchIO {
        if (skipIfInProgress && (stateFlow.value is ApiUiState.Standby || stateFlow.value is ApiUiState.Success)) {
            return@launchIO
        }

        stateFlow.value = ApiUiState.Standby
        apiCall().collect { result ->
            result
                .onSuccess { data ->
                    stateFlow.value = ApiUiState.Success(transform(data))
                }.onError {
                    stateFlow.value = ApiUiState.Error
                }
        }
    }
}

/**
 * Extension function for simple API calls without transformation
 */
inline fun <T> BaseViewModel.handleApiCall(
    stateFlow: MutableStateFlow<ApiUiState<T>>,
    crossinline apiCall: suspend () -> Flow<Result<T>>,
    skipIfInProgress: Boolean = true,
) {
    handleApiCall(stateFlow, apiCall, { it }, skipIfInProgress)
}

/**
 * Extension function to handle UI state changes with loading management
 */
inline fun <T> ApiUiState<T>.handleUiState(
    crossinline onNone: () -> Unit = {},
    crossinline onStandby: () -> Unit = {},
    crossinline onSuccess: (T) -> Unit = {},
    crossinline onError: () -> Unit = {},
) {
    when (this) {
        is ApiUiState.None -> onNone()
        is ApiUiState.Standby -> onStandby()
        is ApiUiState.Success -> onSuccess(data)
        is ApiUiState.Error -> onError()
    }
}
