package pion.tech.pionbase.feature.resultHiddenApp.adapter

import androidx.databinding.ViewDataBinding
import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppListItem
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import pion.tech.pionbase.data.model.hiddenApp.formatAppSize
import pion.tech.pionbase.databinding.ItemHiddenAppBinding
import pion.tech.pionbase.databinding.ItemHiddenAppHeaderBinding
import pion.tech.pionbase.util.AppUtils
import pion.tech.pionbase.util.setPreventDoubleClick

class HiddenAppsAdapter :
    BaseListAdapter<HiddenAppListItem, ViewDataBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem ->
                when {
                    oldItem is HiddenAppListItem.Header && newItem is HiddenAppListItem.Header ->
                        oldItem.type == newItem.type

                    oldItem is HiddenAppListItem.AppItem && newItem is HiddenAppListItem.AppItem ->
                        oldItem.app.packageName == newItem.app.packageName

                    else -> false
                }
            },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_APP_ITEM = 1
    }

    interface Listener {
        fun onAppClick(item: HiddenAppUIModel)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is HiddenAppListItem.Header -> VIEW_TYPE_HEADER
            is HiddenAppListItem.AppItem -> VIEW_TYPE_APP_ITEM
        }

    override fun getLayoutRes(viewType: Int): Int =
        when (viewType) {
            VIEW_TYPE_HEADER -> R.layout.item_hidden_app_header
            VIEW_TYPE_APP_ITEM -> R.layout.item_hidden_app
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }

    override fun bindView(
        binding: ViewDataBinding,
        item: HiddenAppListItem,
        position: Int,
    ) {
        when (item) {
            is HiddenAppListItem.Header -> {
                if (binding is ItemHiddenAppHeaderBinding) {
                    binding.tvHeaderTitle.text = item.title
                }
            }

            is HiddenAppListItem.AppItem -> {
                if (binding is ItemHiddenAppBinding) {
                    val app = item.app
                    binding.apply {
                        // Basic app info
                        tvAppName.text = app.appName
                        ivAppIcon.setImageDrawable(
                            AppUtils.loadAppIcon(
                                root.context,
                                app.packageName,
                            ),
                        )
                        tvAppSize.text = app.formatAppSize()

                        root.setPreventDoubleClick {
                            listener?.onAppClick(app)
                        }
                    }
                }
            }
        }
    }
}
