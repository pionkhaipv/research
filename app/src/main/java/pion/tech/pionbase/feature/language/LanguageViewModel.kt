package pion.tech.pionbase.feature.language

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.data.model.language.LanguageDtoModel
import pion.tech.pionbase.data.model.language.toPresentation
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepository
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.onSuccess
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel
    @Inject
    constructor(
        private val repository: LanguageRepository,
    ) : BaseViewModel() {
        private val _languageData = MutableStateFlow<List<LanguageUIModel>>(emptyList())
        val languageData = _languageData.asStateFlow()

        init {
            loadLanguages()
        }

        private fun loadLanguages() {
            launchIO {
                repository.getLanguage().collect { result ->
                    result.onSuccess { languageList ->
                        _languageData.value = languageList.map { item -> item.toPresentation() }
                    }
                }
            }
        }

        fun selectLanguage(item: LanguageUIModel) {
            _languageData.value =
                _languageData.value.map { language ->
                    language.copy(isSelected = language.localeCode == item.localeCode)
                }
        }

        fun getSelectedLanguage(): LanguageUIModel? = _languageData.value.firstOrNull { it.isSelected }
    }
