package pion.tech.pionbase.data.repository.hiddenAppRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppDtoModel
import pion.tech.pionbase.util.Result

interface HiddenAppsRepository {
    fun getHiddenApps(): Flow<Result<List<HiddenAppDtoModel>>>
}