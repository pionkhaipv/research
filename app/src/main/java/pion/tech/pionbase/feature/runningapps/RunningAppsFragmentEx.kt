package pion.tech.pionbase.feature.runningapps

import androidx.recyclerview.widget.LinearLayoutManager
import pion.tech.pionbase.feature.runningapps.adapter.RunningAppsAdapter
import pion.tech.pionbase.util.setPreventDoubleClick

fun RunningAppsFragment.initView() {
    adapter = RunningAppsAdapter()
    binding.rvRunningApps.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = this@initView.adapter
        setHasFixedSize(true)
    }
}

fun RunningAppsFragment.setupToolbar() {
    binding.toolbar.setNavigationOnClickListener {
        requireActivity().onBackPressed()
    }
}

fun RunningAppsFragment.setupSwipeRefresh() {
    binding.swipeRefresh.setOnRefreshListener {
        viewModel.refreshApps()
    }
}

fun RunningAppsFragment.setupClickListeners() {
    binding.btnRefresh.setPreventDoubleClick {
        viewModel.refreshApps()
    }
}

fun RunningAppsFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun RunningAppsFragment.backEvent() {
    navigator.navigateUp()
}

fun RunningAppsFragment.updateAppCount(count: Int) {
    binding.tvAppCount.text = "Total: $count apps running"
}
