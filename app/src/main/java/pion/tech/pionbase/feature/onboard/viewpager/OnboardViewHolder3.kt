package pion.tech.pionbase.feature.onboard.viewpager

import pion.tech.pionbase.databinding.PagerOnboard3Binding
import pion.tech.pionbase.feature.onboard.OnboardFragment
import pion.tech.pionbase.feature.onboard.adapter.OnboardAdapter

class OnboardViewHolder3(
    val binding: PagerOnboard3Binding,
    val onboardAdapter: OnboardAdapter,
    val fragment: OnboardFragment?,
) : OnboardAdapter.OnboardViewHolder(binding.root) {
    override fun bind() {
//        fragment.context?.let { ctx ->
//            binding.tvSwipe.text =
//                "< " + ctx.getString(R.string.swipe_to_continue) + " >"
//        }
    }
}
