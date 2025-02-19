package com.piontech.domain.usecase

import com.piontech.domain.model.AppCategoryData
import com.piontech.domain.repository.ApiRepository
import com.piontech.domain.util.Result
import kotlinx.coroutines.flow.Flow

class AppCategoryUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(): Flow<Result<List<AppCategoryData>>> {
        return repository.getAppCategory()
    }
}