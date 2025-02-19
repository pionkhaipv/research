package com.piontech.data.repository

import com.piontech.data.api.ApiInterface
import com.piontech.data.model.template.toDomain
import com.piontech.data.model.toDomain
import com.piontech.domain.model.AppCategoryData
import com.piontech.domain.model.TemplateData
import com.piontech.domain.repository.ApiRepository
import com.piontech.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApiRepositoryImpl(private val apiInterface: ApiInterface) : ApiRepository {
    override suspend fun getAppCategory(): Flow<Result<List<AppCategoryData>>> {
        return flow {
            try {
                emit(Result.Success(apiInterface.getAppCategory().dataResponse.map { it.toDomain() }))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
    }

    override suspend fun getTemplateData(categoryId: String): Flow<Result<List<TemplateData>>> {
        return flow {
            try {
                emit(Result.Success(apiInterface.getAllTemplate(categoryId).dataResponse.map { it.customField.toDomain() }))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
    }

}