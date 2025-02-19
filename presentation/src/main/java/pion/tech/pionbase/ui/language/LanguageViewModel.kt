package pion.tech.pionbase.ui.language

import android.util.Log
import com.piontech.domain.usecase.LanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import pion.tech.pionbase.ui.common.BaseViewModel
import pion.tech.pionbase.ui.common.launchIO
import pion.tech.pionbase.mapper.toPresentation
import pion.tech.pionbase.model.LanguageUIModel
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languageUseCase: LanguageUseCase
) : BaseViewModel() {

    private val _languageData = MutableStateFlow<List<LanguageUIModel>>(emptyList())
    val languageData = _languageData.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        launchIO {
            languageUseCase.invoke().catch {
                it.printStackTrace()
            }.collect {
                _languageData.value = it.map { item -> item.toPresentation() }
            }
        }
    }

    fun selectLanguage(selectedIndex: Int) {
        _languageData.value = _languageData.value.mapIndexed { index, language ->
            language.copy(isSelected = index == selectedIndex)
        }
    }

    fun getSelectedLanguage(): LanguageUIModel? {
        return _languageData.value.firstOrNull { it.isSelected }
    }
}