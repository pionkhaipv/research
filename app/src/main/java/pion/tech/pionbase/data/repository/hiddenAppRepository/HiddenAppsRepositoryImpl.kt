package pion.tech.pionbase.data.repository.hiddenAppRepository

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppDtoModel
import pion.tech.pionbase.util.Result
import java.io.File

class HiddenAppsRepositoryImpl(
    @ApplicationContext private val context: Context,
) : HiddenAppsRepository {
    
    /**
     * Calculate the size of an app by its package name and source directory
     * Uses StorageStatsManager on Android 8.0+ for accurate size calculation
     * Falls back to traditional methods on older Android versions
     */
    private fun calculateAppSize(packageName: String, sourceDir: String): Long {
        return try {
            // Try to use StorageStatsManager on Android 8.0+ (API 26+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
                    val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as? StorageStatsManager
                    
                    if (storageStatsManager != null) {
                        // Get storage stats for the app using UUID_DEFAULT (internal storage)
                        val storageStats = storageStatsManager.queryStatsForUid(StorageManager.UUID_DEFAULT, appInfo.uid)
                        
                        // Calculate total size (app + data + cache)
                        val totalSize = storageStats.appBytes + storageStats.dataBytes + storageStats.cacheBytes
                        
                        // Log the breakdown for debugging
                        Log.d("AppSizeCalculation", "Package: $packageName")
                        Log.d("AppSizeCalculation", "App bytes: ${storageStats.appBytes}")
                        Log.d("AppSizeCalculation", "Data bytes: ${storageStats.dataBytes}")
                        Log.d("AppSizeCalculation", "Cache bytes: ${storageStats.cacheBytes}")
                        
                        // Check for OBB files (expansion files for large apps)
                        var obbSize = 0L
                        val obbDir = Environment.getExternalStorageDirectory().toString() + "/Android/obb/" + packageName
                        val obbDirFile = File(obbDir)
                        if (obbDirFile.exists() && obbDirFile.isDirectory) {
                            obbSize = calculateDirSize(obbDirFile)
                            Log.d("AppSizeCalculation", "OBB size: $obbSize")
                        }
                        
                        val finalSize = if (totalSize + obbSize > 0) totalSize + obbSize else 1024L
                        Log.d("AppSizeCalculation", "Final calculated size: $finalSize")
                        return finalSize
                    }
                } catch (e: Exception) {
                    // If StorageStatsManager fails, fall back to traditional method
                }
            }
            
            // Fall back to traditional method for older Android versions or if StorageStatsManager fails
            Log.d("AppSizeCalculation", "Using fallback method for package: $packageName")
            var totalSize = 0L
            
            // Get the package info to access more details
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            
            // 1. Get APK file size - this is the most reliable method
            val apkFile = File(sourceDir)
            var apkSize = 0L
            if (apkFile.exists()) {
                if (apkFile.isFile) {
                    // If sourceDir points directly to the APK file
                    apkSize = apkFile.length()
                    totalSize += apkSize
                } else if (apkFile.isDirectory) {
                    // If sourceDir points to a directory containing the APK
                    apkSize = calculateDirSize(apkFile)
                    totalSize += apkSize
                }
            }
            Log.d("AppSizeCalculation", "APK size: $apkSize")
            
            // 2. Try to get split APK sizes if they exist (for app bundles)
            var splitApksSize = 0L
            try {
                val splitSourceDirs = packageInfo.applicationInfo?.splitSourceDirs
                if (splitSourceDirs != null) {
                    for (splitSourceDir in splitSourceDirs) {
                        val splitFile = File(splitSourceDir)
                        if (splitFile.exists() && splitFile.isFile) {
                            val splitSize = splitFile.length()
                            splitApksSize += splitSize
                            totalSize += splitSize
                        }
                    }
                }
                Log.d("AppSizeCalculation", "Split APKs size: $splitApksSize")
            } catch (e: Exception) {
                // Ignore if we can't access split APKs
                Log.d("AppSizeCalculation", "Error accessing split APKs: ${e.message}")
            }
            
            // 3. Try to access data directory
            var dataDirSize = 0L
            try {
                val dataDir = packageInfo.applicationInfo?.dataDir
                if (dataDir != null) {
                    val dataDirFile = File(dataDir)
                    if (dataDirFile.exists() && dataDirFile.canRead()) {
                        dataDirSize = calculateDirSize(dataDirFile)
                        totalSize += dataDirSize
                    }
                }
                Log.d("AppSizeCalculation", "Data directory size: $dataDirSize")
            } catch (e: Exception) {
                // Ignore if we can't access data directory
                Log.d("AppSizeCalculation", "Error accessing data directory: ${e.message}")
            }
            
            // 4. Try to access cache directory
            var cacheDirSize = 0L
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val cacheDir = packageInfo.applicationInfo?.deviceProtectedDataDir
                    if (cacheDir != null) {
                        val cacheDirFile = File(cacheDir)
                        if (cacheDirFile.exists() && cacheDirFile.canRead()) {
                            cacheDirSize = calculateDirSize(cacheDirFile)
                            totalSize += cacheDirSize
                        }
                    }
                }
                Log.d("AppSizeCalculation", "Cache directory size: $cacheDirSize")
            } catch (e: Exception) {
                // Ignore if we can't access cache directory
                Log.d("AppSizeCalculation", "Error accessing cache directory: ${e.message}")
            }
            
            // 5. Try to access external storage for this app
            var externalStorageSize = 0L
            try {
                // Primary external storage
                val externalFilesDir = context.getExternalFilesDir(null)?.parentFile
                if (externalFilesDir != null) {
                    val appExternalDir = File(externalFilesDir, packageName)
                    if (appExternalDir.exists() && appExternalDir.canRead()) {
                        val externalDirSize = calculateDirSize(appExternalDir)
                        externalStorageSize += externalDirSize
                        totalSize += externalDirSize
                    }
                }
                
                // Check for OBB files (expansion files for large apps)
                val obbDir = Environment.getExternalStorageDirectory().toString() + "/Android/obb/" + packageName
                val obbDirFile = File(obbDir)
                if (obbDirFile.exists() && obbDirFile.isDirectory) {
                    val obbSize = calculateDirSize(obbDirFile)
                    externalStorageSize += obbSize
                    totalSize += obbSize
                    Log.d("AppSizeCalculation", "OBB size: $obbSize")
                }
                
                // Check for external cache
                val externalCacheDir = context.externalCacheDir?.parentFile
                if (externalCacheDir != null) {
                    val appExternalCacheDir = File(externalCacheDir, packageName)
                    if (appExternalCacheDir.exists() && appExternalCacheDir.canRead()) {
                        val externalCacheSize = calculateDirSize(appExternalCacheDir)
                        externalStorageSize += externalCacheSize
                        totalSize += externalCacheSize
                    }
                }
                Log.d("AppSizeCalculation", "External storage size: $externalStorageSize")
            } catch (e: Exception) {
                // Ignore if we can't access external storage
                Log.d("AppSizeCalculation", "Error accessing external storage: ${e.message}")
            }
            
            // Return the total size (or at least what we could access)
            val finalSize = if (totalSize > 0) totalSize else 1024L // Return at least 1KB if we found something
            Log.d("AppSizeCalculation", "Final calculated size (fallback): $finalSize")
            finalSize
        } catch (e: Exception) {
            // If all else fails, return a default size to avoid showing 0 bytes
            Log.e("AppSizeCalculation", "Error calculating app size for $packageName: ${e.message}")
            1024L // 1KB as minimum size
        }
    }
    
    /**
     * Calculate directory size recursively
     */
    private fun calculateDirSize(dir: File): Long {
        var size = 0L
        if (dir.exists()) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    size += if (file.isDirectory) {
                        calculateDirSize(file)
                    } else {
                        file.length()
                    }
                }
            }
        }
        return size
    }
    override fun getHiddenApps(): Flow<Result<List<HiddenAppDtoModel>>> =
        flow<Result<List<HiddenAppDtoModel>>> {
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
                            appSize = calculateAppSize(appInfo.packageName, appInfo.sourceDir)
                        )
                    }.sortedBy { it.appName.lowercase() }

            emit(Result.Success(hiddenApps))
        }.catch { exception ->
            emit(Result.Error(exception))
        }.flowOn(Dispatchers.IO)
}