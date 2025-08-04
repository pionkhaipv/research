package pion.tech.pionbase.feature.resultHiddenApp

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import com.piontech.core.utils.parcelableArrayList
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
    ) {

    private val adapter = HiddenAppsAdapter()

    override fun init(view: View) {
        // Get hidden apps from Bundle using extension function
        val hiddenApps = arguments?.parcelableArrayList<HiddenAppUIModel>("hiddenApps") ?: emptyList()
        viewModel.setHiddenApps(hiddenApps)

        initRecyclerView()
        setupAdapterClickListeners(adapter)
        scanAgainEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.hiddenApps.collectFlowOnView(viewLifecycleOwner) { hiddenApps ->
            updateUI(hiddenApps)
        }
    }

    private fun initRecyclerView() {
        binding.rvHiddenApps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ResultHiddenAppFragment.adapter
        }
    }

    private fun updateUI(hiddenApps: List<pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel>) {
        val count = hiddenApps.size
        binding.tvCount.text = if (count == 1) {
            "1 hidden app found"
        } else {
            "$count hidden apps found"
        }

        if (hiddenApps.isEmpty()) {
            binding.rvHiddenApps.visibility = View.GONE
            binding.tvEmptyMessage.visibility = View.VISIBLE
        } else {
            binding.rvHiddenApps.visibility = View.VISIBLE
            binding.tvEmptyMessage.visibility = View.GONE
            adapter.submitList(hiddenApps)
        }
    }
}
