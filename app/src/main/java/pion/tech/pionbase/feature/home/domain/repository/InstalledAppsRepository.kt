package pion.tech.pionbase.feature.home.domain.repository

import pion.tech.pionbase.feature.home.domain.model.InstalledAppData

interface InstalledAppsRepository {
    suspend fun getInstalledApps(): List<InstalledAppData>
}
