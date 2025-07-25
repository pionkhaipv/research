package pion.tech.pionbase.feature.runningapps.adapter

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.runningApp.BackgroundType
import pion.tech.pionbase.data.model.runningApp.RunningAppUIModel
import pion.tech.pionbase.databinding.ItemRunningAppBinding

class RunningAppsAdapter :
    BaseListAdapter<RunningAppUIModel, ItemRunningAppBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.packageName == newItem.packageName },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    var onAppClick: ((RunningAppUIModel) -> Unit)? = null
    var onAppLongClick: ((RunningAppUIModel) -> Unit)? = null

    override fun getLayoutRes(viewType: Int): Int = R.layout.item_running_app

    override fun bindView(
        binding: ItemRunningAppBinding,
        item: RunningAppUIModel,
        position: Int,
    ) {
        binding.apply {
            // Basic app info
            tvAppName.text = item.appName
            tvPackageName.text = item.packageName
            ivAppIcon.setImageDrawable(item.appIcon)

            // Memory usage with better formatting
            val memoryMB = item.memoryUsage / (1024 * 1024)
            tvMemoryUsage.text =
                when {
                    memoryMB >= 1000 -> "%.1f GB".format(memoryMB / 1024.0)
                    memoryMB >= 1 -> "$memoryMB MB"
                    else -> "< 1 MB"
                }

            // Background type with colors and icon
            setupBackgroundType(item.backgroundType)

            // App type with color distinction
            tvAppType.text = if (item.isSystemApp) "System" else "User"
            tvAppType.setTextColor(
                if (item.isSystemApp) {
                    ContextCompat.getColor(root.context, android.R.color.holo_orange_dark)
                } else {
                    ContextCompat.getColor(root.context, android.R.color.holo_blue_dark)
                },
            )

            // Priority indicator
            setupPriorityIndicator(item.backgroundType.priority)

            // Click listeners
            root.setOnClickListener { onAppClick?.invoke(item) }
            root.setOnLongClickListener {
                onAppLongClick?.invoke(item)
                true
            }
        }
    }

    private fun ItemRunningAppBinding.setupBackgroundType(backgroundType: BackgroundType) {
        tvBackgroundType.text = backgroundType.description

        // Colors based on priority level
        val (textColor, backgroundColor) =
            when (backgroundType) {
                BackgroundType.WALLPAPER_SERVICE -> {
                    Pair(Color.WHITE, ContextCompat.getColor(root.context, android.R.color.holo_purple))
                }
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

        // Round corners for background type
        tvBackgroundType.setPadding(16, 8, 16, 8)
    }

    private fun ItemRunningAppBinding.setupPriorityIndicator(priority: Int) {
        viewPriorityIndicator.setBackgroundColor(
            when (priority) {
                5 -> ContextCompat.getColor(root.context, android.R.color.holo_purple) // Wallpaper Service
                4 -> ContextCompat.getColor(root.context, android.R.color.holo_red_dark) // Critical
                3 -> ContextCompat.getColor(root.context, android.R.color.holo_orange_dark) // High
                2 -> ContextCompat.getColor(root.context, android.R.color.holo_blue_dark) // Medium
                1 -> ContextCompat.getColor(root.context, android.R.color.holo_green_dark) // Low
                else -> ContextCompat.getColor(root.context, android.R.color.darker_gray) // None
            },
        )
    }
}
