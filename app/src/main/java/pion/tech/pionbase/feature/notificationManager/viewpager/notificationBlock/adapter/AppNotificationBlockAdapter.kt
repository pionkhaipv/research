package pion.tech.pionbase.feature.notificationManager.viewpager.notificationBlock.adapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionUIModel
import pion.tech.pionbase.databinding.ItemAppNotificationBlockBinding
import pion.tech.pionbase.util.AppUtils
import pion.tech.pionbase.util.preventDrag
import pion.tech.pionbase.util.setPreventDoubleClick

class AppNotificationBlockAdapter :
    BaseListAdapter<AppNotificationPermissionUIModel, ItemAppNotificationBlockBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.packageName == newItem.packageName },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    interface Listener {
        fun onTogglePermission(
            item: AppNotificationPermissionUIModel,
            isEnabled: Boolean,
        )
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getLayoutRes(viewType: Int): Int = R.layout.item_app_notification_block

    @SuppressLint("ClickableViewAccessibility")
    override fun bindView(
        binding: ItemAppNotificationBlockBinding,
        item: AppNotificationPermissionUIModel,
        position: Int,
    ) {
        if (item.appIcon != null) {
            binding.ivAppIcon.setImageDrawable(item.appIcon)
        } else {
            binding.ivAppIcon.setImageResource(R.drawable.ic_app_scan_default)
        }

        binding.tvAppName.text = item.appName

        binding.switchNotification.isChecked = !item.isNotificationEnabled

        binding.switchNotification.isClickable = false

        binding.switchNotification.preventDrag()

        binding.switchNotification.setPreventDoubleClick {
            listener?.onTogglePermission(item, !binding.switchNotification.isChecked)
            if (binding.switchNotification.isChecked) {
                binding.switchNotification.isChecked = false
            }
        }
    }
}
