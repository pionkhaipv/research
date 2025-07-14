package pion.tech.pionbase.feature.runningapps.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pion.tech.pionbase.databinding.ItemRunningAppBinding
import pion.tech.pionbase.feature.runningapps.presentation.model.RunningAppUIModel

class RunningAppsAdapter : ListAdapter<RunningAppUIModel, RunningAppsAdapter.RunningAppViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RunningAppViewHolder {
        val binding = ItemRunningAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunningAppViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RunningAppViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class RunningAppViewHolder(
        private val binding: ItemRunningAppBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(app: RunningAppUIModel) {
            binding.apply {
                tvAppName.text = app.appName
                tvPackageName.text = app.packageName
                ivAppIcon.setImageDrawable(app.appIcon)

                // Hiển thị memory usage
                val memoryMB = app.memoryUsage / (1024 * 1024)
                tvMemoryUsage.text = "$memoryMB MB"

                // Hiển thị loại app
                tvAppType.text = if (app.isSystemApp) "System App" else "User App"
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<RunningAppUIModel>() {
        override fun areItemsTheSame(
            oldItem: RunningAppUIModel,
            newItem: RunningAppUIModel,
        ): Boolean = oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(
            oldItem: RunningAppUIModel,
            newItem: RunningAppUIModel,
        ): Boolean = oldItem == newItem
    }
}
