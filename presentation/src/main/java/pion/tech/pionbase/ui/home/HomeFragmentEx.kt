package pion.tech.pionbase.ui.home

import androidx.activity.addCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.ui.home.bottomSheet.DemoBottomSheet
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HomeFragment.initView() {
    binding.rvMain.adapter = adapter
    commonViewModel.getApiData()
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
