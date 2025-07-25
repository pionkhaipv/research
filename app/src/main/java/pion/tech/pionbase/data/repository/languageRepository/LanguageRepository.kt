package pion.tech.pionbase.data.repository.languageRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.language.LanguageDtoModel

interface LanguageRepository {
    fun getLanguage(): Flow<List<LanguageDtoModel>>
}
