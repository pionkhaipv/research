package pion.tech.pionbase.framework.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pion.tech.pionbase.util.PrefUtil
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val prefUtil: PrefUtil
) : ViewModel() {

    val isPremium = MutableLiveData(prefUtil.IS_PREMIUM)


}