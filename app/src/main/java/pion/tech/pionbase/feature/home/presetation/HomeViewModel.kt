package pion.tech.pionbase.feature.home.presetation

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pion.tech.pionbase.app.domain.repository.DataStoreRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : BaseViewModel() {

    private val _countValue = MutableStateFlow(0)
    val countValue: StateFlow<Int> = _countValue

    fun plusValue() {
        _countValue.value += 1
    }

    suspend fun getIsPremiumValue(): Flow<Boolean> {
        return dataStoreRepository.getIsPremium()
    }


}