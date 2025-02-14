package pion.tech.pionbase.framework.presentation.onboard.viewpager

import pion.tech.pionbase.databinding.PagerOnboard2Binding
import pion.tech.pionbase.framework.presentation.onboard.OnboardFragment
import pion.tech.pionbase.framework.presentation.onboard.adapter.OnboardAdapter

class OnboardViewHolder2(
    val binding: PagerOnboard2Binding,
    val onboardAdapter: OnboardAdapter,
    val fragment: OnboardFragment?
) :
    OnboardAdapter.OnboardViewHolder(binding.root) {

    override fun bind() {
    }


}