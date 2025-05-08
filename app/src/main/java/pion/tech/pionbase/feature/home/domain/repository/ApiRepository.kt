package pion.tech.pionbase.feature.home.domain.repository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.feature.home.domain.model.AppCategoryData
import pion.tech.pionbase.feature.home.domain.model.TemplateData
import pion.tech.pionbase.util.Result

interface ApiRepository {
    suspend fun getAppCategory(): Flow<Result<List<AppCategoryData>>>
    suspend fun getTemplateData(categoryId: String): Flow<Result<List<TemplateData>>>
}