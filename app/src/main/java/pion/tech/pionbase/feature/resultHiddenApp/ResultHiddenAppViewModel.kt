package pion.tech.pionbase.feature.resultHiddenApp

import android.content.Context
import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.hiddenApp.AppType
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppListItem
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import javax.inject.Inject

@HiltViewModel
class ResultHiddenAppViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : BaseViewModel() {
        private val _hiddenApps = MutableStateFlow<List<HiddenAppUIModel>>(emptyList())
        private val _groupedApps = MutableStateFlow<List<HiddenAppListItem>>(emptyList())
        val groupedApps = _groupedApps.asStateFlow()

        fun setHiddenApps(apps: List<HiddenAppUIModel>) {
            _hiddenApps.value = apps
            groupAppsByType(apps)
        }

        private fun groupAppsByType(apps: List<HiddenAppUIModel>) {
            val userApps = apps.filter { !it.isSystemApp }
            val systemApps = apps.filter { it.isSystemApp }

            val items = mutableListOf<HiddenAppListItem>()

            // Add user-installed apps section if not empty
            if (userApps.isNotEmpty()) {
                items.add(
                    HiddenAppListItem.Header(
                        context.getString(R.string.user_installed_apps),
                        AppType.USER_INSTALLED,
                    ),
                )
                userApps.forEach { app ->
                    items.add(HiddenAppListItem.AppItem(app))
                }
            }

            // Add system apps section if not empty
            if (systemApps.isNotEmpty()) {
                items.add(
                    HiddenAppListItem.Header(
                        context.getString(R.string.system_apps),
                        AppType.SYSTEM,
                    ),
                )
                systemApps.forEach { app ->
                    items.add(HiddenAppListItem.AppItem(app))
                }
            }

            _groupedApps.value = items
        }
    }
