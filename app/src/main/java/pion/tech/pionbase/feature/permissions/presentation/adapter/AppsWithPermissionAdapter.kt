package pion.tech.pionbase.feature.permissions.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pion.tech.pionbase.databinding.ItemAppPermissionBinding
import pion.tech.pionbase.feature.permissions.presentation.model.AppPermissionUIModel

class AppsWithPermissionAdapter : ListAdapter<AppPermissionUIModel, AppsWithPermissionAdapter.AppViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AppViewHolder {
        val binding = ItemAppPermissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AppViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class AppViewHolder(
        private val binding: ItemAppPermissionBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(app: AppPermissionUIModel) {
            binding.apply {
                tvAppName.text = app.appName
                tvPackageName.text = app.packageName
                ivAppIcon.setImageDrawable(app.appIcon)

                // Hiển thị trạng thái permission
                tvPermissionStatus.text = if (app.isGranted) "Đã cấp" else "Đã từ chối"
                tvPermissionStatus.setTextColor(
                    if (app.isGranted) {
                        android.graphics.Color.GREEN
                    } else {
                        android.graphics.Color.RED
                    },
                )

                // Hiển thị loại app
                tvAppType.text = if (app.isSystemApp) "System App" else "User App"
                tvAppType.setTextColor(
                    if (app.isSystemApp) {
                        android.graphics.Color.GRAY
                    } else {
                        android.graphics.Color.BLACK
                    },
                )
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AppPermissionUIModel>() {
        override fun areItemsTheSame(
            oldItem: AppPermissionUIModel,
            newItem: AppPermissionUIModel,
        ): Boolean = oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(
            oldItem: AppPermissionUIModel,
            newItem: AppPermissionUIModel,
        ): Boolean = oldItem == newItem
    }
}
