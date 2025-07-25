package pion.tech.pionbase.data.repository.runningAppsRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.runningApp.RunningAppDtoModel
import pion.tech.pionbase.util.Result

interface RunningAppsRepository {
    fun getRunningApps(): Flow<Result<List<RunningAppDtoModel>>>

    fun refreshRunningApps(): Flow<Result<List<RunningAppDtoModel>>>
}
