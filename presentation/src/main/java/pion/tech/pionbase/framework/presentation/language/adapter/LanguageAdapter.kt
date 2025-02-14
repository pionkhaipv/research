package pion.tech.pionbase.framework.presentation.language.adapter

import coil.load
import com.piontech.domain.model.Language
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.ItemLanguageBinding
import pion.tech.pionbase.framework.presentation.common.BaseListAdapter
import pion.tech.pionbase.framework.presentation.common.createDiffCallback
import pion.tech.pionbase.framework.presentation.model.LanguageUIModel
import pion.tech.pionbase.util.setPreventDoubleClick


class LanguageAdapter : BaseListAdapter<LanguageUIModel, ItemLanguageBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem.localeCode == newItem.localeCode },
        areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {

    interface Listener {
        fun onClickLanguage(item: LanguageUIModel, position: Int)
    }

    fun getCurrentLanguageSelected(): LanguageUIModel? {
        return currentList.firstOrNull { it.isSelected }
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getLayoutRes(viewType: Int): Int = R.layout.item_language
    override fun bindView(binding: ItemLanguageBinding, item: LanguageUIModel, position: Int) {
        binding.tvName.text = item.nameCountry
        if (item.isSelected) {
            binding.root.setBackgroundResource(R.drawable.bg_language_selected)
        } else {
            binding.root.setBackgroundResource(R.drawable.bg_language_unselected)
        }
        binding.ivFlag.load(item.thumbnail)
        binding.root.setPreventDoubleClick {
            currentList.forEach { it.isSelected = false }
            currentList[position].isSelected = true
            listener?.onClickLanguage(item, position)
        }
    }
}
