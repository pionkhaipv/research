package pion.tech.pionbase.data.repository.installedAppRepository

import pion.tech.pionbase.data.model.installedApp.InstalledAppDto

interface InstalledAppsRepository {
    suspend fun getInstalledApps(): List<InstalledAppDto>
}
