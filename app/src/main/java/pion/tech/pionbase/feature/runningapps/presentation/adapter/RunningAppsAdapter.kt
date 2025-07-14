package pion.tech.pionbase.feature.runningapps.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.ItemRunningAppBinding
import pion.tech.pionbase.feature.runningapps.presentation.model.BackgroundType
import pion.tech.pionbase.feature.runningapps.presentation.model.RunningAppUIModel

class RunningAppsAdapter : ListAdapter<RunningAppUIModel, RunningAppsAdapter.RunningAppViewHolder>(DiffCallback()) {
    var onAppClick: ((RunningAppUIModel) -> Unit)? = null
    var onAppLongClick: ((RunningAppUIModel) -> Unit)? = null

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
        holder.bind(getItem(position), onAppClick, onAppLongClick)
    }

    class RunningAppViewHolder(
        private val binding: ItemRunningAppBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            app: RunningAppUIModel,
            onAppClick: ((RunningAppUIModel) -> Unit)?,
            onAppLongClick: ((RunningAppUIModel) -> Unit)?,
        ) {
            binding.apply {
                // Basic app info
                tvAppName.text = app.appName
                tvPackageName.text = app.packageName
                ivAppIcon.setImageDrawable(app.appIcon)

                // Memory usage với format đẹp hơn
                val memoryMB = app.memoryUsage / (1024 * 1024)
                tvMemoryUsage.text =
                    when {
                        memoryMB >= 1000 -> "%.1f GB".format(memoryMB / 1024.0)
                        memoryMB >= 1 -> "$memoryMB MB"
                        else -> "< 1 MB"
                    }

                // Background type với màu sắc và icon
                setupBackgroundType(app.backgroundType)

                // App type với màu phân biệt
                tvAppType.text = if (app.isSystemApp) "System" else "User"
                tvAppType.setTextColor(
                    if (app.isSystemApp) {
                        ContextCompat.getColor(root.context, android.R.color.holo_orange_dark)
                    } else {
                        ContextCompat.getColor(root.context, android.R.color.holo_blue_dark)
                    },
                )

                // Priority indicator
                setupPriorityIndicator(app.backgroundType.priority)

                // Click listeners
                root.setOnClickListener { onAppClick?.invoke(app) }
                root.setOnLongClickListener {
                    onAppLongClick?.invoke(app)
                    true
                }
            }
        }

        private fun setupBackgroundType(backgroundType: BackgroundType) {
            binding.apply {
                tvBackgroundType.text = backgroundType.description

                // Màu sắc theo mức độ ưu tiên
                val (textColor, backgroundColor) =
                    when (backgroundType) {
                        BackgroundType.FOREGROUND_SERVICE -> {
                            Pair(Color.WHITE, ContextCompat.getColor(root.context, android.R.color.holo_red_dark))
                        }
                        BackgroundType.BACKGROUND_SERVICE -> {
                            Pair(Color.WHITE, ContextCompat.getColor(root.context, android.R.color.holo_orange_dark))
                        }
                        BackgroundType.ACTIVE_PROCESS -> {
                            Pair(Color.WHITE, ContextCompat.getColor(root.context, android.R.color.holo_blue_dark))
                        }
                        BackgroundType.RECENT_USAGE -> {
                            Pair(Color.BLACK, ContextCompat.getColor(root.context, android.R.color.holo_green_light))
                        }
                        BackgroundType.NONE -> {
                            Pair(Color.BLACK, ContextCompat.getColor(root.context, android.R.color.darker_gray))
                        }
                    }

                tvBackgroundType.setTextColor(textColor)
                tvBackgroundType.setBackgroundColor(backgroundColor)

                // Làm tròn góc cho background type
                tvBackgroundType.setPadding(16, 8, 16, 8)
            }
        }

        private fun setupPriorityIndicator(priority: Int) {
            binding.apply {
                viewPriorityIndicator.setBackgroundColor(
                    when (priority) {
                        4 -> ContextCompat.getColor(root.context, android.R.color.holo_red_dark) // Critical
                        3 -> ContextCompat.getColor(root.context, android.R.color.holo_orange_dark) // High
                        2 -> ContextCompat.getColor(root.context, android.R.color.holo_blue_dark) // Medium
                        1 -> ContextCompat.getColor(root.context, android.R.color.holo_green_dark) // Low
                        else -> ContextCompat.getColor(root.context, android.R.color.darker_gray) // None
                    },
                )
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
