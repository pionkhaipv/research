package pion.tech.pionbase.feature.home.presetation

import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.presentation.CommonViewModel
import pion.tech.pionbase.app.presentation.GetAppCategoryUiState
import pion.tech.pionbase.app.presentation.GetTemplateUiState
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.presetation.adapter.DemoMultipleAdapter
import pion.tech.pionbase.feature.home.presetation.dialog.DemoDialog
import pion.tech.pionbase.util.displayToast

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel, CommonViewModel>(
    FragmentHomeBinding::inflate,
    HomeViewModel::class.java,
    CommonViewModel::class.java
), DemoDialog.Listener {

    val adapter = DemoMultipleAdapter()
    override fun init(view: View) {
        logger.logScreen("home_show")
        logger.logEvent("home_view")
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
                    val templateCategoryId =
                        it.listAppCategory.firstOrNull { item -> item.name == "Template" }?.id
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
