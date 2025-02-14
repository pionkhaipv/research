package pion.tech.pionbase.framework.presentation.onboard

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.FragmentOnboardBinding
import pion.tech.pionbase.framework.presentation.common.BaseFragment
import pion.tech.pionbase.framework.presentation.onboard.adapter.OnboardAdapter

@AndroidEntryPoint
class OnboardFragment : BaseFragment<FragmentOnboardBinding, OnboardViewModel>(
    FragmentOnboardBinding::inflate,
    OnboardViewModel::class.java
), OnboardAdapter.Listener {
    var adapter: OnboardAdapter? = null
    override fun init(view: View) {
        initView()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDoneOnboard() {
        safeNav(R.id.onboardFragment, R.id.action_onboardFragment_to_homeFragment)
    }
}