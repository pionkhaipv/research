package pion.tech.pionbase.ui.home

import com.piontech.domain.usecase.PreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pion.tech.pionbase.ui.common.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesUseCase: PreferencesUseCase,
) : BaseViewModel() {

    private val _countValue = MutableStateFlow(0)
    val countValue: StateFlow<Int> = _countValue

    fun plusValue() {
        _countValue.value += 1
    }

    fun getIsPremiumValue()= preferencesUseCase.getPremiumStatus()
}