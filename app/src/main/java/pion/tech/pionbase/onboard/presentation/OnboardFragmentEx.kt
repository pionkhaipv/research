package pion.tech.pionbase.onboard.presentation

import androidx.activity.addCallback
import pion.tech.pionbase.onboard.presentation.adapter.OnboardAdapter

fun OnboardFragment.initView(){
    adapter = OnboardAdapter()
    adapter!!.setOnboardFragment(this)
    adapter!!.setListener(this)
    binding.vpMain.adapter = adapter
}

fun OnboardFragment.onBackEvent() {
    activity?.onBackPressedDispatcher?.addCallback(this, true) {
        backEvent()
    }
}

fun OnboardFragment.backEvent() {
}
