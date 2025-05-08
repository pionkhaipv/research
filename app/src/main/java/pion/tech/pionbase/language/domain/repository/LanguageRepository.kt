package pion.tech.pionbase.language.domain.repository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.language.domain.model.LanguageData

interface LanguageRepository {
    fun getLanguage(): Flow<List<LanguageData>>
}