package com.piontech.domain.repository

import com.piontech.domain.model.AppCategoryData
import com.piontech.domain.model.TemplateData
import kotlinx.coroutines.flow.Flow

interface ApiRepository {
    suspend fun getAppCategory(): Flow<List<AppCategoryData>>
    suspend fun getTemplateData(categoryId: String): Flow<List<TemplateData>>
}