package pion.tech.pionbase.feature.resultConcernApp.viewpager.concernsTab

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.concernApp.ConcernAppUIModel
import javax.inject.Inject

@HiltViewModel
class ConcernsTabViewModel
    @Inject
    constructor() : BaseViewModel() {
        private val _concernApps = MutableStateFlow<List<ConcernAppUIModel>>(emptyList())
        val concernApps = _concernApps.asStateFlow()

        fun setConcernApps(apps: List<ConcernAppUIModel>) {
            _concernApps.value = apps
        }

        fun getConcernAppsCount(): Int = _concernApps.value.size
    }
