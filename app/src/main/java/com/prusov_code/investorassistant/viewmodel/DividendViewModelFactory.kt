package com.prusov_code.investorassistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prusov_code.investorassistant.repository.DividendRepository

class DividendViewModelFactory(
    private val repository: DividendRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DividendViewModel::class.java)) {
            return DividendViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}