package pion.tech.pionbase.feature.notificationManager

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.repository.notificationRepository.NotificationRepository
import pion.tech.pionbase.util.onSuccess
import javax.inject.Inject

@HiltViewModel
class NotificationManagerViewModel
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) : BaseViewModel() {
        private val _isNotificationListenerEnabled = MutableStateFlow(false)
        val isNotificationListenerEnabled: StateFlow<Boolean> =
            _isNotificationListenerEnabled.asStateFlow()

        fun checkNotificationListenerStatus() {
            launchIO {
                notificationRepository.isNotificationListenerEnabled().collect { result ->
                    result.onSuccess { isEnabled ->
                        _isNotificationListenerEnabled.value = isEnabled
                    }
                }
            }
        }

        fun toggleNotificationListener(enabled: Boolean) {
            launchIO {
                notificationRepository.setNotificationListenerEnabled(enabled).collect { result ->
                    result.onSuccess { isEnabled ->
                        _isNotificationListenerEnabled.value = isEnabled
                    }
                }
            }
        }

        private val _modeNotificationManager =
            MutableStateFlow<ModeNotificationManager>(ModeNotificationManager.Stats)
        val modeNotificationManager = _modeNotificationManager.asStateFlow()

        fun setModeNotificationManager(mode: ModeNotificationManager) {
            _modeNotificationManager.value = mode
        }
    }

enum class ModeNotificationManager {
    Stats,
    Block,
}
