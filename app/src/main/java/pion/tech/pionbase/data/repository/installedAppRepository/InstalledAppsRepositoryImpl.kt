package pion.tech.pionbase.data.repository.installedAppRepository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pion.tech.pionbase.data.model.installedApp.InstalledAppDtoModel
import javax.inject.Inject

class InstalledAppsRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : InstalledAppsRepository {
        override suspend fun getInstalledApps(): List<InstalledAppDtoModel> =
            withContext(Dispatchers.IO) {
                val packageManager = context.packageManager
                val installedPackages =
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

                installedPackages
                    .map { appInfo ->
                        InstalledAppDtoModel(
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
            }
    }
