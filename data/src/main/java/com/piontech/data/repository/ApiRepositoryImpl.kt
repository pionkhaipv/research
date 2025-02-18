package com.piontech.data.repository

import com.piontech.data.api.ApiInterface
import com.piontech.data.model.template.toDomain
import com.piontech.data.model.toDomain
import com.piontech.domain.model.AppCategoryData
import com.piontech.domain.model.TemplateData
import com.piontech.domain.repository.ApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApiRepositoryImpl(private val apiInterface: ApiInterface) : ApiRepository {
    override suspend fun getAppCategory(): Flow<List<AppCategoryData>> {
        return flow {
            emit(apiInterface.getAppCategory().dataResponse.map { it.toDomain() })
        }
    }

    override suspend fun getTemplateData(categoryId: String): Flow<List<TemplateData>> {
        return flow {
            emit(apiInterface.getAllTemplate(categoryId).dataResponse.map { it.customField.toDomain() })
        }
    }

}