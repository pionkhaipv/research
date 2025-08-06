package pion.tech.pionbase.feature.notificationManager.viewpager.notificationStats

import com.piontech.core.utils.collectFlowOnView
import pion.tech.pionbase.R
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.displayToast

fun NotificationStatsFragment.initView() {
    binding.rvMain.adapter = adapter

    // Set up test notification button
    binding.btnTestNotification.setOnClickListener {
        testNotificationService()
    }
}

private fun NotificationStatsFragment.testNotificationService() {
    viewModel.sendTestNotification().collectFlowOnView(viewLifecycleOwner) { result ->
        when (result) {
            is Result.Success<Boolean> -> {
                displayToast(getString(R.string.test_notification_sent))
            }

            is Result.Error -> {
                displayToast(
                    getString(
                        R.string.test_notification_failed,
                        result.error.message ?: "Unknown error",
                    ),
                )
            }

            else -> {}
        }
    }
}
