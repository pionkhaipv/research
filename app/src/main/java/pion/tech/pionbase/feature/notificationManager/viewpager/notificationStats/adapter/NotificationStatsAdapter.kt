package pion.tech.pionbase.feature.notificationManager.viewpager.notificationStats.adapter

import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.notification.NotificationUIModel
import pion.tech.pionbase.databinding.ItemAppNotificationStatsBinding
import pion.tech.pionbase.util.DateTimeUtils

class NotificationStatsAdapter :
    BaseListAdapter<NotificationUIModel, ItemAppNotificationStatsBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.packageName == newItem.packageName },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    override fun getLayoutRes(viewType: Int): Int = R.layout.item_app_notification_stats

    override fun bindView(
        binding: ItemAppNotificationStatsBinding,
        item: NotificationUIModel,
        position: Int,
    ) {
        binding.tvAppName.text = item.appName
        binding.tvNotificationCount.text = item.notificationCount.toString()
        if (item.icon != null) {
            binding.ivAppIcon.setImageDrawable(item.icon)
        } else {
            binding.ivAppIcon.setImageResource(R.drawable.ic_app_scan_default)
        }
        binding.tvTime.text = DateTimeUtils.formatTimestamp(binding.root.context, item.timestamp)
    }
}
