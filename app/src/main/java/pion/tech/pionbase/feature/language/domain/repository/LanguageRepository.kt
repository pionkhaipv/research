package pion.tech.pionbase.feature.language.domain.repository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.feature.language.domain.model.LanguageData

interface LanguageRepository {
    fun getLanguage(): Flow<List<LanguageData>>
}