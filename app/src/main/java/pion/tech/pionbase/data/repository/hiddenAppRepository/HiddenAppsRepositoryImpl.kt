package pion.tech.pionbase.data.repository.hiddenAppRepository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppDtoModel
import pion.tech.pionbase.util.Result

class HiddenAppsRepositoryImpl(
    @ApplicationContext private val context: Context,
) : HiddenAppsRepository {
    override fun getHiddenApps(): Flow<Result<List<HiddenAppDtoModel>>> =
        flow {
            try {
                val packageManager = context.packageManager
                val installedPackages =
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

                // Get all apps that have launcher intents
                val launcherIntent = Intent(Intent.ACTION_MAIN, null)
                launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                val launcherApps = packageManager.queryIntentActivities(launcherIntent, 0)
                val launcherPackageNames = launcherApps.map { it.activityInfo.packageName }.toSet()

                // Filter apps that don't have launcher intents (hidden apps)
                val hiddenApps =
                    installedPackages
                        .filter { appInfo ->
                            // Exclude system apps and apps that have launcher intents
                            !launcherPackageNames.contains(appInfo.packageName) &&
                                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                        }
                        .map { appInfo ->
                            HiddenAppDtoModel(
                                packageName = appInfo.packageName,
                                appName =
                                    try {
                                        packageManager.getApplicationLabel(appInfo).toString()
                                    } catch (e: Exception) {
                                        appInfo.packageName
                                    },
                                icon =
                                    try {
                                        packageManager.getApplicationIcon(appInfo)
                                    } catch (e: Exception) {
                                        null
                                    },
                                versionName =
                                    try {
                                        packageManager.getPackageInfo(appInfo.packageName, 0).versionName
                                    } catch (e: Exception) {
                                        null
                                    },
                                isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                            )
                        }.sortedBy { it.appName.lowercase() }

                emit(Result.Success(hiddenApps))
            } catch (exception: Exception) {
                emit(Result.Error(exception))
            }
        }.flowOn(Dispatchers.IO)
}