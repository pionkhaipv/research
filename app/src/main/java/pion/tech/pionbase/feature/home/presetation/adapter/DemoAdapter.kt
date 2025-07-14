package pion.tech.pionbase.feature.home.presetation.adapter

import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import com.piontech.core.utils.loadImage
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.ItemDummyBinding
import pion.tech.pionbase.feature.home.presetation.AppWithOverlayPermission

class DemoAdapter :
    BaseListAdapter<AppWithOverlayPermission, ItemDummyBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.packageName == newItem.packageName },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    override fun getLayoutRes(viewType: Int): Int = R.layout.item_dummy

    override fun bindView(
        binding: ItemDummyBinding,
        item: AppWithOverlayPermission,
        position: Int,
    ) {
        binding.name.text = item.appName
        binding.tvStatus.text = if (item.hasOverlayPermission) "Có quyền overlay" else "Không có quyền overlay"
        binding.ivMain.loadImage(item.appIcon)
    }
}
