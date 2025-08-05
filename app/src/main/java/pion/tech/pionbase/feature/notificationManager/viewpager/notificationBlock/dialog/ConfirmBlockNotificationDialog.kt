package pion.tech.pionbase.feature.notificationManager.viewpager.notificationBlock.dialog

import android.os.Bundle
import com.piontech.core.base.BaseDialogFragment
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.DialogConfirmBlockNotificationBinding
import pion.tech.pionbase.util.setPreventDoubleClick

class ConfirmBlockNotificationDialog :
    BaseDialogFragment<DialogConfirmBlockNotificationBinding>(R.layout.dialog_confirm_block_notification) {
    interface Listener {
        fun onConfirmBlock()
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
        binding.btnCancel.setPreventDoubleClick {
            dismiss()
        }

        binding.btnBlock.setPreventDoubleClick {
            listener?.onConfirmBlock()
            dismiss()
        }
    }
}
