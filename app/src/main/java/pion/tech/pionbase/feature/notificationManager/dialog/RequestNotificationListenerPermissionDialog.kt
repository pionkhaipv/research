package pion.tech.pionbase.feature.notificationManager.dialog

import android.os.Bundle
import com.piontech.core.base.BaseDialogFragment
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.DialogRequestNotificationListenerPermissinoBinding
import pion.tech.pionbase.util.setPreventDoubleClick

class RequestNotificationListenerPermissionDialog :
    BaseDialogFragment<DialogRequestNotificationListenerPermissinoBinding>(
        R.layout.dialog_request_notification_listener_permissino,
    ) {
    interface Listener {
        fun onOpenSetting()
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun addEvent(savedInstanceState: Bundle?) {
        super.addEvent(savedInstanceState)
        binding.btnClose.setPreventDoubleClick {
            dismiss()
        }

        binding.btnOpenSetting.setPreventDoubleClick {
            listener?.onOpenSetting()
            dismiss()
        }
    }
}
