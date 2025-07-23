package pion.tech.pionbase.feature.home.presetation

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import pion.tech.pionbase.app.domain.repository.DataStoreRepository
import pion.tech.pionbase.feature.home.presetation.model.InstalledAppUIModel
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleLocalDataCall
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    @ApplicationContext private val context: Context,
) : BaseViewModel() {

    private val _countValue = MutableStateFlow(0)
    val countValue: StateFlow<Int> = _countValue

    private val _installedAppsUiState = MutableStateFlow<UiState<List<InstalledAppUIModel>>>(UiState.None)
    val installedAppsUiState = _installedAppsUiState.asStateFlow()

    fun plusValue() {
        _countValue.value += 1
    }

    suspend fun getIsPremiumValue(): Flow<Boolean> {
        return dataStoreRepository.getIsPremium()
    }

    fun getInstalledApps() {
        handleLocalDataCall(
            stateFlow = _installedAppsUiState,
            dataCall = { getInstalledAppsFromDevice() }
        )
    }

    private suspend fun getInstalledAppsFromDevice(): List<InstalledAppUIModel> {
        return withContext(Dispatchers.IO) {
            val packageManager = context.packageManager
            val installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            installedPackages.map { appInfo ->
                InstalledAppUIModel(
                    packageName = appInfo.packageName,
                    appName = try {
                        packageManager.getApplicationLabel(appInfo).toString()
                    } catch (e: Exception) {
                        appInfo.packageName
                    },
                    icon = try {
                        packageManager.getApplicationIcon(appInfo)
                    } catch (e: Exception) {
                        null
                    },
                    versionName = try {
                        packageManager.getPackageInfo(appInfo.packageName, 0).versionName
                    } catch (e: Exception) {
                        null
                    },
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }.sortedBy { it.appName.lowercase() }
        }
    }
}
