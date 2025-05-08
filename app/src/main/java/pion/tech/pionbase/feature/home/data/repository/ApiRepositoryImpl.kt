package pion.tech.pionbase.feature.home.data.repository

import kotlinx.coroutines.Dispatchers
import pion.tech.pionbase.feature.home.domain.model.template.TemplateData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.feature.home.data.api.ApiInterface
import pion.tech.pionbase.feature.home.domain.model.AppCategoryData
import pion.tech.pionbase.feature.home.domain.repository.ApiRepository
import pion.tech.pionbase.util.Result

class ApiRepositoryImpl(private val apiInterface: ApiInterface) : ApiRepository {
    override suspend fun getAppCategory(): Flow<Result<List<AppCategoryData>>> {
        return flow<Result<List<AppCategoryData>>> {
            emit(Result.Success(apiInterface.getAppCategory().dataResponse))
        }.catch {
            emit(Result.Error(it))
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getTemplateData(categoryId: String): Flow<Result<List<TemplateData>>> {
        return flow<Result<List<TemplateData>>> {
            emit(Result.Success(apiInterface.getAllTemplate(categoryId).dataResponse.map { it.customField }))
        }.catch {
            emit(Result.Error(it))
        }.flowOn(Dispatchers.IO)
    }

}