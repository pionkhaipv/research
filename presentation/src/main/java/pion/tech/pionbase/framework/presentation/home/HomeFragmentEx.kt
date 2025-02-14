package pion.tech.pionbase.framework.presentation.home

import pion.tech.pionbase.framework.presentation.home.bottomSheet.DemoBottomSheet
import pion.tech.pionbase.framework.presentation.home.dialog.DemoDialog
import pion.tech.pionbase.util.BundleKey
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.parcelable
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HomeFragment.initView() {
    dummyEntity = arguments?.parcelable(BundleKey.KEY_DUMMY_ENTITY)
    binding.rvMain.adapter = adapter
    binding.tvHome.text = "Is premium " + viewModel.getIsPremiumValue().toString()
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
