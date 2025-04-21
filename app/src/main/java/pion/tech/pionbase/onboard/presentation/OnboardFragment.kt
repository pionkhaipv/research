package pion.tech.pionbase.onboard.presentation

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentOnboardBinding
import pion.tech.pionbase.core.presentation.common.base.BaseFragment
import pion.tech.pionbase.core.presentation.navigator.NavigationRoute
import pion.tech.pionbase.onboard.presentation.adapter.OnboardAdapter

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
        navigator.navigateTo(NavigationRoute.ONBOARD_TO_HOME)
    }
}