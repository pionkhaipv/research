package pion.tech.pionbase.feature.onboard.viewpager

import pion.tech.pionbase.databinding.PagerOnboard1Binding
import pion.tech.pionbase.feature.onboard.OnboardFragment
import pion.tech.pionbase.feature.onboard.adapter.OnboardAdapter

class OnboardViewHolder1(
    val binding: PagerOnboard1Binding,
    val onboardAdapter: OnboardAdapter,
    val fragment: OnboardFragment?,
) : OnboardAdapter.OnboardViewHolder(binding.root) {
    override fun bind() {
    }
}
