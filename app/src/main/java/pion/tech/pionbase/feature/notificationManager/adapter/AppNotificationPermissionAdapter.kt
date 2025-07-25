package pion.tech.pionbase.feature.notificationManager.adapter

import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionUIModel
import pion.tech.pionbase.databinding.ItemAppNotificationPermissionBinding

class AppNotificationPermissionAdapter(
    private val onTogglePermission: (String, Boolean) -> Unit
) : BaseListAdapter<AppNotificationPermissionUIModel, ItemAppNotificationPermissionBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem.packageName == newItem.packageName },
        areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {

    override fun getLayoutRes(viewType: Int): Int = R.layout.item_app_notification_permission

    override fun bindView(
        binding: ItemAppNotificationPermissionBinding,
        item: AppNotificationPermissionUIModel,
        position: Int
    ) {
        // Set app icon
        if (item.icon != null) {
            binding.ivAppIcon.setImageDrawable(item.icon)
        } else {
            binding.ivAppIcon.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        // Set app name
        binding.tvAppName.text = item.appName

        // Set package name
        binding.tvPackageName.text = item.packageName

        // Set switch state without triggering listener
        binding.switchNotification.setOnCheckedChangeListener(null)
        binding.switchNotification.isChecked = item.isNotificationEnabled

        // Set switch listener
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            onTogglePermission(item.packageName, isChecked)
        }
    }
}
