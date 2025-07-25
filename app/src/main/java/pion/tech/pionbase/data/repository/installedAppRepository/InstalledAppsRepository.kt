package pion.tech.pionbase.data.repository.installedAppRepository

import pion.tech.pionbase.data.model.installedApp.InstalledAppDtoModel

interface InstalledAppsRepository {
    suspend fun getInstalledApps(): List<InstalledAppDtoModel>
}
