package pion.tech.pionbase.feature.resultHiddenApp

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import pion.tech.pionbase.databinding.FragmentResultHiddenAppBinding
import pion.tech.pionbase.feature.resultHiddenApp.adapter.HiddenAppsAdapter

@AndroidEntryPoint
class ResultHiddenAppFragment :
    BaseFragment<FragmentResultHiddenAppBinding, ResultHiddenAppViewModel, CommonViewModel>(
        FragmentResultHiddenAppBinding::inflate,
        ResultHiddenAppViewModel::class.java,
        CommonViewModel::class.java,
    ),
    HiddenAppsAdapter.Listener {
    val adapter = HiddenAppsAdapter()

    override fun init(view: View) {
        initView()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.groupedApps.collectFlowOnView(viewLifecycleOwner) { groupedApps ->
            adapter.submitList(groupedApps)
            if (groupedApps.isEmpty()) {
                binding.rvHiddenApps.isVisible = false
                binding.llEmptyItem.isVisible = true
            } else {
                binding.rvHiddenApps.isVisible = true
                binding.llEmptyItem.isVisible = false
            }
        }
    }

    override fun onAppClick(item: HiddenAppUIModel) {
        openAppSettings(item)
    }
}
