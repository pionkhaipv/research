package pion.tech.pionbase.feature.resultConcernApp.viewpager.concernsTab

import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentConcernsTabBinding
import pion.tech.pionbase.feature.resultConcernApp.adapter.ConcernAppsAdapter

@AndroidEntryPoint
class ConcernsTabFragment :
    BaseFragment<FragmentConcernsTabBinding, ConcernsTabViewModel, CommonViewModel>(
        FragmentConcernsTabBinding::inflate,
        ConcernsTabViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    val adapter = ConcernAppsAdapter()

    override fun init(view: View) {
        initView()
        initRecyclerViewEvent()
        setupAdapterClickListenersEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.concernApps.collectFlowOnView(viewLifecycleOwner) { concernApps ->
            updateUIEvent(concernApps)
        }
    }
}
