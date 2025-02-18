package pion.tech.pionbase.ui.onboard

import androidx.activity.addCallback
import pion.tech.pionbase.ui.onboard.adapter.OnboardAdapter

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
