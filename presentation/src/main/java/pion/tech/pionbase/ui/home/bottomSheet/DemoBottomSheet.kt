package pion.tech.pionbase.ui.home.bottomSheet

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.BottomSheetDemoBinding
import pion.tech.pionbase.ui.common.BaseBottomSheetDialogFragment

@AndroidEntryPoint
class DemoBottomSheet :
    BaseBottomSheetDialogFragment<BottomSheetDemoBinding>(R.layout.bottom_sheet_demo) {
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
    }
}