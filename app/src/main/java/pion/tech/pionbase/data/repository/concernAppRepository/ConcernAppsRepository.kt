package pion.tech.pionbase.data.repository.concernAppRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.concernApp.ConcernAppDtoModel
import pion.tech.pionbase.util.Result

interface ConcernAppsRepository {
    fun getConcernApps(): Flow<Result<List<ConcernAppDtoModel>>>
}