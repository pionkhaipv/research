package pion.tech.pionbase.home.presetation

import pion.tech.pionbase.main.presentation.Route
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import pion.tech.pionbase.home.presetation.bottomSheet.DemoBottomSheet

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
    onSystemBack {
        backEvent()
    }
}

fun HomeFragment.backEvent() {
}

fun HomeFragment.settingEvent() {
    binding.btnSetting.setPreventDoubleClickScaleView {
        navigator.navigateTo(Route.HOME_TO_SETTING)
    }
}
