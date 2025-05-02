package com.prusov_code.investorassistant.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prusov_code.investorassistant.repository.UserRepository
import com.prusov_code.investorassistant.security.SessionManager

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(
            UserRepository(context),
            SessionManager(context)
        ) as T
    }
}