package com.piontech.domain.usecase

import com.piontech.domain.model.TemplateData
import com.piontech.domain.repository.ApiRepository
import com.piontech.domain.util.Result
import kotlinx.coroutines.flow.Flow

class TemplateUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(categoryId: String): Flow<Result<List<TemplateData>>> {
        return repository.getTemplateData(categoryId = categoryId)
    }
}