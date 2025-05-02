package com.prusov_code.investorassistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prusov_code.investorassistant.repository.DividendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DividendViewModel(
    private val repository: DividendRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DividendState>(DividendState.Loading)
    val state: StateFlow<DividendState> = _state

    init {
        loadDividends()
    }

    private fun loadDividends() {
        viewModelScope.launch {
            repository.allDividends
                .catch { e ->
                    _state.value = DividendState.Error("Ошибка загрузки: ${e.message}")
                }
                .collect { dividends ->
                    _state.value = if (dividends.isEmpty()) {
                        DividendState.Empty
                    } else {
                        DividendState.Success(dividends)
                    }
                }
        }
    }
}