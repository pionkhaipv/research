package pion.tech.pionbase.framework.presentation.language

import android.view.View
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentLanguageBinding
import pion.tech.pionbase.databinding.FragmentSplashBinding
import pion.tech.pionbase.framework.presentation.common.BaseFragment
import pion.tech.pionbase.framework.presentation.language.adapter.LanguageAdapter
import pion.tech.pionbase.framework.presentation.model.LanguageUIModel
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.setPreventDoubleClick


@AndroidEntryPoint
class LanguageFragment : BaseFragment<FragmentLanguageBinding, LanguageViewModel>(
    FragmentLanguageBinding::inflate,
    LanguageViewModel::class.java
), LanguageAdapter.Listener {

    val adapter = LanguageAdapter()

    override fun init(view: View) {
        initView()
        applyEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.languageData.collectFlowOnView(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onClickLanguage(item: LanguageUIModel, position: Int) {
        binding.ivDone.isVisible = true
    }

}
