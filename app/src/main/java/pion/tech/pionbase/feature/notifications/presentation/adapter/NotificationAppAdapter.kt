package pion.tech.pionbase.feature.notifications.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pion.tech.pionbase.databinding.ItemNotificationAppBinding
import pion.tech.pionbase.feature.notifications.presentation.model.NotificationAppInfo

class NotificationAppAdapter(
    private val onItemClick: (NotificationAppInfo) -> Unit,
) : ListAdapter<NotificationAppInfo, NotificationAppAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ItemNotificationAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemNotificationAppBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(app: NotificationAppInfo) {
            binding.apply {
                tvAppName.text = app.appName
                tvPackageName.text = app.packageName

                // Set app icon
                if (app.appIcon != null) {
                    ivAppIcon.setImageDrawable(app.appIcon)
                } else {
                    // Set default icon if app icon is null
                    ivAppIcon.setImageResource(android.R.drawable.sym_def_app_icon)
                }

                // Set notification status
                switchNotifications.isChecked = app.notificationsEnabled
                tvNotificationStatus.text =
                    if (app.notificationsEnabled) {
                        "Notifications enabled"
                    } else {
                        "Notifications disabled"
                    }

                // Handle switch toggle
                switchNotifications.setOnCheckedChangeListener { _, isChecked ->
                    // Open app's notification settings when switch is toggled
                    onItemClick(app)
                }

                // Handle item click
                root.setOnClickListener {
                    onItemClick(app)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<NotificationAppInfo>() {
        override fun areItemsTheSame(
            oldItem: NotificationAppInfo,
            newItem: NotificationAppInfo,
        ): Boolean = oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(
            oldItem: NotificationAppInfo,
            newItem: NotificationAppInfo,
        ): Boolean = oldItem == newItem
    }
}
