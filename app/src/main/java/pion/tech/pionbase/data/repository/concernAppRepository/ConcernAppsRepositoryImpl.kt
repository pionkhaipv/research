package pion.tech.pionbase.data.repository.concernAppRepository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.data.model.concernApp.ConcernAppDtoModel
import pion.tech.pionbase.data.model.concernApp.DangerLevel
import pion.tech.pionbase.util.Result

class ConcernAppsRepositoryImpl(
    @ApplicationContext private val context: Context,
) : ConcernAppsRepository {

    // Define dangerous permissions that pose high security risks
    private val dangerousPermissions = setOf(
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.READ_CALL_LOG",
        "android.permission.WRITE_CALL_LOG",
        "android.permission.CALL_PHONE",
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.RECORD_AUDIO",
        "android.permission.CAMERA",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.MANAGE_EXTERNAL_STORAGE",
        "android.permission.SYSTEM_ALERT_WINDOW",
        "android.permission.WRITE_SETTINGS",
        "android.permission.DEVICE_ADMIN"
    )

    // Define medium-risk permissions
    private val mediumRiskPermissions = setOf(
        "android.permission.ACCESS_NETWORK_STATE",
        "android.permission.ACCESS_WIFI_STATE",
        "android.permission.CHANGE_WIFI_STATE",
        "android.permission.BLUETOOTH",
        "android.permission.BLUETOOTH_ADMIN",
        "android.permission.GET_ACCOUNTS",
        "android.permission.USE_FINGERPRINT",
        "android.permission.USE_BIOMETRIC",
        "android.permission.VIBRATE",
        "android.permission.WAKE_LOCK"
    )

    override fun getConcernApps(): Flow<Result<List<ConcernAppDtoModel>>> = flow {
        try {
            val packageManager = context.packageManager
            val installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            // Get all apps that have launcher intents
            val launcherIntent = Intent(Intent.ACTION_MAIN, null)
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val launcherApps = packageManager.queryIntentActivities(launcherIntent, 0)
            val launcherPackageNames = launcherApps.map { it.activityInfo.packageName }.toSet()

            val concernApps = installedPackages
                .filter { appInfo ->
                    // Only include apps that have launcher intents (can be launched)
                    launcherPackageNames.contains(appInfo.packageName)
                }
                .mapNotNull { appInfo ->
                    try {
                        val packageInfo = packageManager.getPackageInfo(
                            appInfo.packageName,
                            PackageManager.GET_PERMISSIONS
                        )

                        val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
                        val dangerLevel = calculateDangerLevel(permissions)

                        ConcernAppDtoModel(
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
                            isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                            permissions = permissions,
                            dangerLevel = dangerLevel
                        )
                    } catch (e: Exception) {
                        null // Skip apps that can't be processed
                    }
                }
                .sortedWith(compareByDescending<ConcernAppDtoModel> { it.dangerLevel.ordinal }.thenBy { it.appName.lowercase() })

            emit(Result.Success(concernApps))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }.flowOn(Dispatchers.IO)

    private fun calculateDangerLevel(permissions: List<String>): DangerLevel {
        val dangerousCount = permissions.count { it in dangerousPermissions }
        val mediumRiskCount = permissions.count { it in mediumRiskPermissions }

        return when {
            dangerousCount >= 3 -> DangerLevel.DANGEROUS
            dangerousCount >= 1 || mediumRiskCount >= 5 -> DangerLevel.MEDIUM
            else -> DangerLevel.SAFE
        }
    }
}
