package pion.tech.pionbase.framework.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pion.tech.pionbase.util.PrefUtil
import javax.inject.Inject


class HomeViewModel(val prefUtil: PrefUtil , val fragment: HomeFragment) : ViewModel() {


    val _isPremium = prefUtil.IS_PREMIUM
    val isPremium = MutableLiveData(_isPremium)

    fun observerPremium(){
        isPremium.observe(fragment.viewLifecycleOwner){
            fragment.binding.viewModel = this
        }
    }



}