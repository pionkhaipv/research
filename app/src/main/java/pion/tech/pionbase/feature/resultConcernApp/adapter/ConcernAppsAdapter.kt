package pion.tech.pionbase.feature.resultConcernApp.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.concernApp.ConcernAppUIModel
import pion.tech.pionbase.data.model.concernApp.DangerLevel
import pion.tech.pionbase.databinding.ItemConcernAppBinding

class ConcernAppsAdapter :
    BaseListAdapter<ConcernAppUIModel, ItemConcernAppBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem: ConcernAppUIModel, newItem: ConcernAppUIModel -> oldItem.packageName == newItem.packageName },
            areContentsTheSame = { oldItem: ConcernAppUIModel, newItem: ConcernAppUIModel -> oldItem == newItem },
        ),
    ) {
    var onAppClick: ((ConcernAppUIModel) -> Unit)? = null
    var onAppLongClick: ((ConcernAppUIModel) -> Unit)? = null

    override fun getLayoutRes(viewType: Int): Int = R.layout.item_concern_app

    override fun bindView(
        binding: ItemConcernAppBinding,
        item: ConcernAppUIModel,
        position: Int,
    ) {
        binding.apply {
            // Basic app info
            tvAppName.text = item.appName
            tvPackageName.text = item.packageName
            ivAppIcon.setImageDrawable(item.loadIcon(root.context))

            // Permissions count
            val permissionCount = item.permissions.size
            tvPermissionsCount.text = if (permissionCount == 1) {
                "1 permission"
            } else {
                "$permissionCount permissions"
            }

            // Danger level with color coding
            tvDangerLevel.text = item.dangerLevel.name
            
            val (dangerColor, dangerTextColor) = when (item.dangerLevel) {
                DangerLevel.DANGEROUS -> Pair(
                    ContextCompat.getColor(root.context, android.R.color.holo_red_light),
                    ContextCompat.getColor(root.context, android.R.color.holo_red_dark)
                )
                DangerLevel.MEDIUM -> Pair(
                    ContextCompat.getColor(root.context, android.R.color.holo_orange_light),
                    ContextCompat.getColor(root.context, android.R.color.holo_orange_dark)
                )
                DangerLevel.SAFE -> Pair(
                    ContextCompat.getColor(root.context, android.R.color.holo_green_light),
                    ContextCompat.getColor(root.context, android.R.color.holo_green_dark)
                )
            }

            // Set danger indicator color
            viewDangerIndicator.setBackgroundColor(dangerColor)
            tvDangerLevel.setTextColor(dangerTextColor)

            // Click listeners
            root.setOnClickListener { onAppClick?.invoke(item) }
            root.setOnLongClickListener {
                onAppLongClick?.invoke(item)
                true
            }
        }
    }
}