package pion.tech.pionbase.data.repository.languageRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.language.LanguageDto

interface LanguageRepository {
    fun getLanguage(): Flow<List<LanguageDto>>
}
