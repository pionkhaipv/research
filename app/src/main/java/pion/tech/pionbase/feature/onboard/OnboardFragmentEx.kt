package pion.tech.pionbase.feature.onboard

import pion.tech.pionbase.feature.onboard.adapter.OnboardAdapter

fun OnboardFragment.initView() {
    adapter = OnboardAdapter()
    adapter!!.setOnboardFragment(this)
    adapter!!.setListener(this)
    binding.vpMain.adapter = adapter
}

fun OnboardFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun OnboardFragment.backEvent() {
}
