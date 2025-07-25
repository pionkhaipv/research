package pion.tech.pionbase.data.repository.languageRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.data.model.language.LanguageDto
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepository

class LanguageRepositoryImpl : LanguageRepository {
    override fun getLanguage(): Flow<List<LanguageDto>> {
        val listLanguageData =
            listOf(
                LanguageDto("https://flagcdn.com/w320/us.png", "English", "en"),
                LanguageDto("https://flagcdn.com/w320/es.png", "Español", "es"),
                LanguageDto("https://flagcdn.com/w320/sa.png", "عربي", "ar"),
                LanguageDto("https://flagcdn.com/w320/pt.png", "Português", "pt"),
                LanguageDto("https://flagcdn.com/w320/fr.png", "Français", "fr"),
                LanguageDto("https://flagcdn.com/w320/de.png", "Deutsch", "de"),
                LanguageDto("https://flagcdn.com/w320/cn.png", "中國人", "zh"),
                LanguageDto("https://flagcdn.com/w320/kr.png", "한국인", "ko"),
                LanguageDto("https://flagcdn.com/w320/jp.png", "日本人", "ja"),
                LanguageDto("https://flagcdn.com/w320/ru.png", "Pусский", "ru"),
                LanguageDto("https://flagcdn.com/w320/vn.png", "Việt Nam", "vi"),
                LanguageDto("https://flagcdn.com/w320/th.png", "ไทย", "th"),
                LanguageDto("https://flagcdn.com/w320/tr.png", "Türkçe", "tr"),
                LanguageDto("https://flagcdn.com/w320/in.png", "हिंदी", "hi"),
                LanguageDto("https://flagcdn.com/w320/uz.png", "O'zbek", "uz"),
                LanguageDto("https://flagcdn.com/w320/it.png", "Italiano", "it"),
                LanguageDto("https://flagcdn.com/w320/pl.png", "Polski", "pl"),
                LanguageDto("https://flagcdn.com/w320/ir.png", "فارسی", "fa"),
                LanguageDto("https://flagcdn.com/w320/ua.png", "Українська Мова", "uk"),
                LanguageDto("https://flagcdn.com/w320/ro.png", "Română", "ro"),
                LanguageDto("https://flagcdn.com/w320/nl.png", "Nederlands", "nl"),
                LanguageDto("https://flagcdn.com/w320/hu.png", "Magyar", "hu"),
                LanguageDto("https://flagcdn.com/w320/bg.png", "Български Език", "bg"),
                LanguageDto("https://flagcdn.com/w320/gr.png", "Ελληνικά", "el"),
            )
        return flow {
            emit(listLanguageData)
        }.catch {
            emit(emptyList())
        }.flowOn(Dispatchers.IO)
    }
}
