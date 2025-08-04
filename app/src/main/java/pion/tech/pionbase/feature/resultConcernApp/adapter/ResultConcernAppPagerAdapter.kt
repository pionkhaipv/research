package pion.tech.pionbase.feature.resultConcernApp.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import pion.tech.pionbase.data.model.concernApp.ConcernAppUIModel
import pion.tech.pionbase.feature.resultConcernApp.viewpager.concernsTab.ConcernsTabFragment
import pion.tech.pionbase.feature.resultConcernApp.viewpager.socialSdkAnalysisTab.SocialSdkAnalysisTabFragment

class ResultConcernAppPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val concernApps: List<ConcernAppUIModel>,
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> {
                // Concerns tab
                ConcernsTabFragment().apply {
                    arguments =
                        Bundle().apply {
                            putParcelableArrayList("concernApps", ArrayList(concernApps))
                        }
                }
            }
            1 -> {
                // Social SDK Analysis tab
                SocialSdkAnalysisTabFragment()
            }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }

    fun getTabTitle(position: Int): String =
        when (position) {
            0 -> "Concerns"
            1 -> "Social SDK Analysis"
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
}
