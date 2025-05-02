package com.prusov_code.investorassistant.viewmodel

import com.prusov_code.investorassistant.model.DepositRecord

sealed class ProfitTableState {
    data class Data(
        val records: List<DepositRecord>,
        val totalProfit: String,
        val weeklyProfit: String,
        val monthlyProfit: String,
        val currentMonthProfit: String,
        val customProfit: String
    ) : ProfitTableState()

    object Loading : ProfitTableState()
    data class Error(val message: String) : ProfitTableState()
    object Empty : ProfitTableState()
}