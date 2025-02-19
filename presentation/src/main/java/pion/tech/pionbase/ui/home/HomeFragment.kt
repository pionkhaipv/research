package pion.tech.pionbase.ui.home

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.ui.common.BaseFragment
import pion.tech.pionbase.ui.common.GetAppCategoryUiState
import pion.tech.pionbase.ui.common.GetTemplateUiState
import pion.tech.pionbase.ui.home.adapter.DemoMultipleAdapter
import pion.tech.pionbase.ui.home.dialog.DemoDialog
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate,
    HomeViewModel::class.java
), DemoDialog.Listener {

    val adapter = DemoMultipleAdapter()
    override fun init(view: View) {
        initView()
        plusEvent()
        settingEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.countValue.collectFlowOnView(viewLifecycleOwner) {
//            binding.tvCount.text = "$it"
        }

        commonViewModel.getCategoryUiState.collectFlowOnView(viewLifecycleOwner) {
            when (it) {
                GetAppCategoryUiState.Error -> {
                    showHideLoading(false)
                }

                GetAppCategoryUiState.None -> {

                }

                GetAppCategoryUiState.Standby -> {
                    showHideLoading(true)
                }

                is GetAppCategoryUiState.Success -> {
                    val templateCategoryId = it.listAppCategory.firstOrNull { item -> item.name == "Template" }?.id
                    if (templateCategoryId != null) {
                        commonViewModel.getTemplate(templateCategoryId)
                    }
                }
            }
        }

        commonViewModel.getTemplateUiState.collectFlowOnView(viewLifecycleOwner) {

            when (it) {
                GetTemplateUiState.Error -> {
                    showHideLoading(false)
                }

                GetTemplateUiState.None -> {
                }

                GetTemplateUiState.Standby -> {
                    showHideLoading(true)
                }

                is GetTemplateUiState.Success -> {
                    showHideLoading(false)
                }
            }
        }
    }

    override fun onDialogPositiveClick() {
    }

    override fun onDialogNegativeClick() {
        displayToast("Hello")
    }

}
