package pion.tech.pionbase.feature.resultConcernApp.viewpager.concernsTab

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.piontech.core.utils.parcelableArrayList
import pion.tech.pionbase.data.model.concernApp.ConcernAppUIModel

fun ConcernsTabFragment.initView() {
    // Get concern apps from Bundle using extension function
    val concernApps = arguments?.parcelableArrayList<ConcernAppUIModel>("concernApps") ?: emptyList()
    viewModel.setConcernApps(concernApps)
}

fun ConcernsTabFragment.initRecyclerViewEvent() {
    binding.rvConcernApps.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = this@initRecyclerViewEvent.adapter
    }
}

fun ConcernsTabFragment.updateUIEvent(concernApps: List<ConcernAppUIModel>) {
    if (concernApps.isEmpty()) {
        binding.rvConcernApps.visibility = View.GONE
        binding.tvEmptyMessage.visibility = View.VISIBLE
    } else {
        binding.rvConcernApps.visibility = View.VISIBLE
        binding.tvEmptyMessage.visibility = View.GONE
        adapter.submitList(concernApps)
    }
}

fun ConcernsTabFragment.setupAdapterClickListenersEvent() {
    adapter.onAppClick = { concernApp ->
        // Handle app click - could show app details
    }

    adapter.onAppLongClick = { concernApp ->
        // Handle app long click - could show context menu
    }
}
