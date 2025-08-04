package pion.tech.pionbase.feature.resultConcernApp

import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import com.piontech.core.utils.parcelableArrayList
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.data.model.concernApp.ConcernAppUIModel
import pion.tech.pionbase.databinding.FragmentResultConcernAppBinding
import pion.tech.pionbase.feature.resultConcernApp.adapter.ResultConcernAppPagerAdapter

@AndroidEntryPoint
class ResultConcernAppFragment :
    BaseFragment<FragmentResultConcernAppBinding, ResultConcernAppViewModel, CommonViewModel>(
        FragmentResultConcernAppBinding::inflate,
        ResultConcernAppViewModel::class.java,
        CommonViewModel::class.java,
    ) {

    lateinit var pagerAdapter: ResultConcernAppPagerAdapter

    override fun init(view: View) {
        initView()
        setupViewPagerEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.concernApps.collectFlowOnView(viewLifecycleOwner) { concernApps ->
            updateUIEvent(concernApps)
        }
    }
}
