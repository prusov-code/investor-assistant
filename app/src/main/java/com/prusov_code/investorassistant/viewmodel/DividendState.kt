package com.prusov_code.investorassistant.viewmodel

import com.prusov_code.investorassistant.model.Dividend

sealed class DividendState {
    object Loading : DividendState()
    data class Success(val dividends: List<Dividend>) : DividendState()
    data class Error(val message: String) : DividendState()
    object Empty : DividendState()
}