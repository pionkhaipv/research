package pion.tech.pionbase.framework.presentation.home

import androidx.activity.addCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.framework.presentation.home.bottomSheet.DemoBottomSheet
import pion.tech.pionbase.framework.presentation.home.dialog.DemoDialog
import pion.tech.pionbase.framework.presentation.splash.SplashFragment
import pion.tech.pionbase.framework.presentation.splash.backEvent
import pion.tech.pionbase.framework.presentation.splash.onBackPressed
import pion.tech.pionbase.util.BundleKey
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.parcelable
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HomeFragment.initView() {
    dummyEntity = arguments?.parcelable(BundleKey.KEY_DUMMY_ENTITY)
    binding.rvMain.adapter = adapter
}

fun HomeFragment.plusEvent() {
    val listString = listOf("so1", "so2", "so3", "so4", "so5")
    adapter.submitList(listString)
    binding.btnPlus.setPreventDoubleClickScaleView {
        viewModel.plusValue()
        val bottomSheet = DemoBottomSheet()
        bottomSheet.show(childFragmentManager)
//        val dialog = DemoDialog.newInstance(dummyTitle = "Day la param1")
//        dialog.setListener(this)
//        dialog.show(childFragmentManager)
    }
}

fun HomeFragment.onBackEvent() {
    activity?.onBackPressedDispatcher?.addCallback(this, true) {
        backEvent()
    }
}

fun HomeFragment.backEvent() {
}

fun HomeFragment.settingEvent() {
    binding.btnSetting.setPreventDoubleClickScaleView {
        safeNav(R.id.homeFragment, R.id.action_homeFragment_to_settingFragment)
    }
}
