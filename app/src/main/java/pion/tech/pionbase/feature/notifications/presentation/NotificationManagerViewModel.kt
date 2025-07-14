package pion.tech.pionbase.feature.notifications.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationManagerViewModel
    @Inject
    constructor() : ViewModel() {
        // Add any specific logic for notification management here
        // For now, the fragment handles most of the logic directly
    }
