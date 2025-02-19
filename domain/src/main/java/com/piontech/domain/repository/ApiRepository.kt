package com.piontech.domain.repository

import com.piontech.domain.model.AppCategoryData
import com.piontech.domain.model.TemplateData
import com.piontech.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface ApiRepository {
    suspend fun getAppCategory(): Flow<Result<List<AppCategoryData>>>
    suspend fun getTemplateData(categoryId: String): Flow<Result<List<TemplateData>>>
}