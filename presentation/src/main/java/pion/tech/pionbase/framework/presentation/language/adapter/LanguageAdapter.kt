package pion.tech.pionbase.framework.presentation.language.adapter

import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.ItemLanguageBinding
import pion.tech.pionbase.framework.presentation.common.BaseListAdapter
import pion.tech.pionbase.framework.presentation.common.createDiffCallback
import pion.tech.pionbase.framework.presentation.model.LanguageModel
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClick

class LanguageAdapter : BaseListAdapter<LanguageModel, ItemLanguageBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem.localeCode == newItem.localeCode },
        areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {

    interface Listener {
        fun onClickLanguage(item: LanguageModel, position: Int)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getLayoutRes(viewType: Int): Int = R.layout.item_language
    override fun bindView(binding: ItemLanguageBinding, item: LanguageModel, position: Int) {
        fun loadBackground() {
            if (item.isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_language_selected)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_language_unselected)
            }
        }

        loadBackground()
        binding.tvName.text = item.nameCountry
        binding.ivFlag.loadImage(item.thumbnail)
        binding.root.setPreventDoubleClick {
            listener?.onClickLanguage(item, position)
        }
    }
}
