package pion.tech.pionbase.framework.presentation.home

import pion.tech.pionbase.util.BundleKey
import pion.tech.pionbase.util.parcelable

fun HomeFragment.initView() {
    dummyEntity = arguments?.parcelable(BundleKey.KEY_DUMMY_ENTITY)
    binding.tvHome.text = dummyEntity?.value
}
