package com.prusov_code.investorassistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow

class CompoundInterestViewModel : ViewModel() {
    private val _state = MutableStateFlow<CalculatorState>(CalculatorState.Input())
    val state: StateFlow<CalculatorState> = _state

    fun updatePrincipal(principal: String) {
        _state.value = when (val current = _state.value) {
            is CalculatorState.Input -> current.copy(principal = principal)
            else -> CalculatorState.Input(principal = principal)
        }
    }

    fun updateRate(rate: String) {
        _state.value = when (val current = _state.value) {
            is CalculatorState.Input -> current.copy(rate = rate)
            else -> CalculatorState.Input(rate = rate)
        }
    }

    fun updateYears(years: String) {
        _state.value = when (val current = _state.value) {
            is CalculatorState.Input -> current.copy(years = years)
            else -> CalculatorState.Input(years = years)
        }
    }

    fun calculate() {
        viewModelScope.launch {
            val input = (_state.value as? CalculatorState.Input) ?: return@launch
            _state.value = CalculatorState.Loading

            try {
                validateInput(input)
                val result = calculateCompoundInterest(input)
                _state.value = CalculatorState.Result(
                    "Итоговая сумма: ${"%.2f".format(result)} ₽"
                )
            } catch (e: Exception) {
                _state.value = CalculatorState.Error(e.message ?: "Ошибка расчета")
            }
        }
    }

    private fun validateInput(input: CalculatorState.Input) {
        with(input) {
            if (principal.isEmpty() || rate.isEmpty() || years.isEmpty()) {
                throw IllegalArgumentException("Заполните все поля")
            }
            if (principal.toDouble() <= 0 || rate.toDouble() <= 0 || years.toInt() <= 0) {
                throw IllegalArgumentException("Значения должны быть больше нуля")
            }
        }
    }

    private fun calculateCompoundInterest(input: CalculatorState.Input): Double {
        return input.principal.toDouble() *
                (1 + input.rate.toDouble() / 100).pow(input.years.toInt())
    }
}