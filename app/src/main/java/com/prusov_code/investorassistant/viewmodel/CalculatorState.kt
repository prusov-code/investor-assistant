package com.prusov_code.investorassistant.viewmodel

sealed class CalculatorState {
    data class Input(
        val principal: String = "",
        val rate: String = "",
        val years: String = ""
    ) : CalculatorState()

    data class Result(val amount: String) : CalculatorState()
    data class Error(val message: String) : CalculatorState()
    object Loading : CalculatorState()
}