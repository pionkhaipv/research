package pion.tech.pionbase.framework.presentation.language

import com.piontech.domain.usecase.LanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.framework.presentation.common.BaseViewModel
import pion.tech.pionbase.framework.presentation.mapper.toPresentation
import pion.tech.pionbase.framework.presentation.model.LanguageModel
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languageUseCase: LanguageUseCase
) : BaseViewModel() {

    private val _languageData = MutableStateFlow<List<LanguageModel>>(emptyList())
    val languageData: StateFlow<List<LanguageModel>> get() = _languageData.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        _languageData.value = languageUseCase.invoke().map { it.toPresentation() }
    }

    fun selectLanguage(selectedIndex: Int) {
        _languageData.value = _languageData.value.mapIndexed { index, language ->
            language.copy(isSelected = index == selectedIndex)
        }
    }

    fun getSelectedLanguage():LanguageModel?{
        return _languageData.value.firstOrNull { it.isSelected }
    }
}