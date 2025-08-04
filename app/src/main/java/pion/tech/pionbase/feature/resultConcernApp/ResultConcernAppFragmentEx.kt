package pion.tech.pionbase.feature.resultConcernApp

import com.google.android.material.tabs.TabLayoutMediator
import com.piontech.core.utils.parcelableArrayList
import pion.tech.pionbase.data.model.concernApp.ConcernAppUIModel
import pion.tech.pionbase.feature.resultConcernApp.adapter.ResultConcernAppPagerAdapter

fun ResultConcernAppFragment.initView() {
    // Get concern apps from Bundle using extension function
    val concernApps = arguments?.parcelableArrayList<ConcernAppUIModel>("concernApps") ?: emptyList()
    viewModel.setConcernApps(concernApps)
}

fun ResultConcernAppFragment.setupViewPagerEvent() {
    val concernApps = arguments?.parcelableArrayList<ConcernAppUIModel>("concernApps") ?: emptyList()
    pagerAdapter = ResultConcernAppPagerAdapter(requireActivity(), concernApps)
    binding.viewPager.adapter = pagerAdapter

    // Connect TabLayout with ViewPager2
    TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
        tab.text = pagerAdapter.getTabTitle(position)
    }.attach()
}

fun ResultConcernAppFragment.updateUIEvent(concernApps: List<ConcernAppUIModel>) {
    val count = concernApps.size
    binding.tvCount.text = if (count == 1) {
        "1 app analyzed"
    } else {
        "$count apps analyzed"
    }
}

fun ResultConcernAppFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun ResultConcernAppFragment.backEvent() {
    // Handle back navigation
    // Could add any cleanup logic here if needed
}
