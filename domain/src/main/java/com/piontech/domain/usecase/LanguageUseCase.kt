package com.piontech.domain.usecase

import com.piontech.domain.model.Language

class LanguageUseCase {
    operator fun invoke(): List<Language> {
        val listLanguage = listOf(
            Language("https://flagcdn.com/w320/us.png", "English", "en"),
            Language("https://flagcdn.com/w320/es.png", "Español", "es"),
            Language("https://flagcdn.com/w320/sa.png", "عربي", "ar"),
            Language("https://flagcdn.com/w320/pt.png", "Português", "pt"),
            Language("https://flagcdn.com/w320/fr.png", "Français", "fr"),
            Language("https://flagcdn.com/w320/de.png", "Deutsch", "de"),
            Language("https://flagcdn.com/w320/cn.png", "中國人", "zh"),
            Language("https://flagcdn.com/w320/kr.png", "한국인", "ko"),
            Language("https://flagcdn.com/w320/jp.png", "日本人", "ja"),
            Language("https://flagcdn.com/w320/ru.png", "Pусский", "ru"),
            Language("https://flagcdn.com/w320/vn.png", "Việt Nam", "vi"),
            Language("https://flagcdn.com/w320/th.png", "ไทย", "th"),
            Language("https://flagcdn.com/w320/tr.png", "Türkçe", "tr"),
            Language("https://flagcdn.com/w320/in.png", "हिंदी", "hi"),
            Language("https://flagcdn.com/w320/uz.png", "O'zbek", "uz"),
            Language("https://flagcdn.com/w320/it.png", "Italiano", "it"),
            Language("https://flagcdn.com/w320/pl.png", "Polski", "pl"),
            Language("https://flagcdn.com/w320/ir.png", "فارسی", "fa"),
            Language("https://flagcdn.com/w320/ua.png", "Українська Мова", "uk"),
            Language("https://flagcdn.com/w320/ro.png", "Română", "ro"),
            Language("https://flagcdn.com/w320/nl.png", "Nederlands", "nl"),
            Language("https://flagcdn.com/w320/hu.png", "Magyar", "hu"),
            Language("https://flagcdn.com/w320/bg.png", "Български Език", "bg"),
            Language("https://flagcdn.com/w320/gr.png", "Ελληνικά", "el")
        )
        return listLanguage
    }
}