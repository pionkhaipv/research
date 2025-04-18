package pion.tech.pionbase.home.presetation.adapter

import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.ItemDummyBinding
import pion.tech.pionbase.core.presentation.common.base.BaseListAdapter
import pion.tech.pionbase.core.presentation.common.base.createDiffCallback

class DemoAdapter : BaseListAdapter<String, ItemDummyBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
        areContentsTheSame = { oldItem, newItem -> true }
    )
) {
    override fun getLayoutRes(viewType: Int): Int = R.layout.item_dummy

    override fun bindView(binding: ItemDummyBinding, item: String, position: Int) {
        binding.name.text = item
    }

}
