package pion.tech.pionbase.feature.resultHiddenApp.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import pion.tech.pionbase.databinding.ItemHiddenAppBinding

class HiddenAppsAdapter :
    BaseListAdapter<HiddenAppUIModel, ItemHiddenAppBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem: HiddenAppUIModel, newItem: HiddenAppUIModel -> oldItem.packageName == newItem.packageName },
            areContentsTheSame = { oldItem: HiddenAppUIModel, newItem: HiddenAppUIModel -> oldItem == newItem },
        ),
    ) {
    var onAppClick: ((HiddenAppUIModel) -> Unit)? = null
    var onAppLongClick: ((HiddenAppUIModel) -> Unit)? = null

    override fun getLayoutRes(viewType: Int): Int = R.layout.item_hidden_app

    override fun bindView(
        binding: ItemHiddenAppBinding,
        item: HiddenAppUIModel,
        position: Int,
    ) {
        binding.apply {
            // Basic app info
            tvAppName.text = item.appName
            tvPackageName.text = item.packageName
            ivAppIcon.setImageDrawable(item.loadIcon(root.context))

            // Version info
            tvVersion.text =
                if (item.versionName != null) {
                    "Version ${item.versionName}"
                } else {
                    "Version unknown"
                }

            // App type with color distinction
            tvAppType.text = if (item.isSystemApp) "System" else "User"
            tvAppType.setTextColor(
                if (item.isSystemApp) {
                    ContextCompat.getColor(root.context, android.R.color.holo_orange_dark)
                } else {
                    ContextCompat.getColor(root.context, android.R.color.holo_blue_dark)
                },
            )

            // Click listeners
            root.setOnClickListener { onAppClick?.invoke(item) }
            root.setOnLongClickListener {
                onAppLongClick?.invoke(item)
                true
            }
        }
    }
}
