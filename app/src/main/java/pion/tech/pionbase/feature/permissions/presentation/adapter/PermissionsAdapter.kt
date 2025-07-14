package pion.tech.pionbase.feature.permissions.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pion.tech.pionbase.databinding.ItemPermissionGroupBinding
import pion.tech.pionbase.feature.permissions.presentation.model.DangerLevel
import pion.tech.pionbase.feature.permissions.presentation.model.PermissionUIModel

class PermissionsAdapter : ListAdapter<PermissionUIModel, PermissionsAdapter.PermissionViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PermissionViewHolder {
        val binding = ItemPermissionGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PermissionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PermissionViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class PermissionViewHolder(
        private val binding: ItemPermissionGroupBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val appsAdapter = AppsWithPermissionAdapter()

        fun bind(permission: PermissionUIModel) {
            binding.apply {
                tvPermissionName.text = permission.displayName
                tvPermissionDescription.text = permission.description
                tvAppCount.text = "${permission.appsWithPermission.size} ứng dụng"

                // Set danger level color
                val dangerColor =
                    when (permission.dangerLevel) {
                        DangerLevel.LOW -> android.R.color.holo_green_light
                        DangerLevel.MEDIUM -> android.R.color.holo_orange_light
                        DangerLevel.HIGH -> android.R.color.holo_red_light
                        DangerLevel.CRITICAL -> android.R.color.holo_red_dark
                    }
                tvDangerLevel.setBackgroundResource(dangerColor)
                tvDangerLevel.text =
                    when (permission.dangerLevel) {
                        DangerLevel.LOW -> "Thấp"
                        DangerLevel.MEDIUM -> "Trung bình"
                        DangerLevel.HIGH -> "Cao"
                        DangerLevel.CRITICAL -> "Nghiêm trọng"
                    }

                // Setup RecyclerView for apps
                rvApps.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = appsAdapter
                    setHasFixedSize(true)
                }

                appsAdapter.submitList(permission.appsWithPermission)

                // Toggle visibility on click
                var isExpanded = false
                root.setOnClickListener {
                    isExpanded = !isExpanded
                    rvApps.visibility =
                        if (isExpanded) {
                            android.view.View.VISIBLE
                        } else {
                            android.view.View.GONE
                        }

                    ivExpandIcon.rotation = if (isExpanded) 180f else 0f
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PermissionUIModel>() {
        override fun areItemsTheSame(
            oldItem: PermissionUIModel,
            newItem: PermissionUIModel,
        ): Boolean = oldItem.permissionName == newItem.permissionName

        override fun areContentsTheSame(
            oldItem: PermissionUIModel,
            newItem: PermissionUIModel,
        ): Boolean = oldItem == newItem
    }
}
