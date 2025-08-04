package pion.tech.pionbase.feature.resultHiddenApp

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import javax.inject.Inject

@HiltViewModel
class ResultHiddenAppViewModel
    @Inject
    constructor() : BaseViewModel() {
        
        private val _hiddenApps = MutableStateFlow<List<HiddenAppUIModel>>(emptyList())
        val hiddenApps = _hiddenApps.asStateFlow()

        fun setHiddenApps(apps: List<HiddenAppUIModel>) {
            _hiddenApps.value = apps
        }

        fun getHiddenAppsCount(): Int {
            return _hiddenApps.value.size
        }
    }