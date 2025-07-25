package pion.tech.pionbase.data.repository.apiRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.appCategory.AppCategoryDtoModel
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.util.Result

interface ApiRepository {
    suspend fun getAppCategory(): Flow<Result<List<AppCategoryDtoModel>>>

    suspend fun getTemplateData(categoryId: String): Flow<Result<List<TemplateDtoModel>>>
}
