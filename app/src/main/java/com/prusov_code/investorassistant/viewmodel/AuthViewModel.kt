package com.prusov_code.investorassistant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prusov_code.investorassistant.model.User
import com.prusov_code.investorassistant.repository.UserRepository
import com.prusov_code.investorassistant.security.PasswordHasher
import com.prusov_code.investorassistant.security.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState
    // Регулярные выражения для валидации
    private val EMAIL_REGEX = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    private val PASSWORD_REGEX = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}\$")
    fun registerUser(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Валидация
            when {
                email.length < 5 -> {
                    _authState.postValue(AuthState.Error("Email должен быть не короче 5 символов"))
                    return@launch
                }
                !EMAIL_REGEX.matches(email) -> {
                    _authState.postValue(AuthState.Error("Некорректный формат email"))
                    return@launch
                }
                password.length < 6 -> {
                    _authState.postValue(AuthState.Error("Пароль должен быть не короче 6 символов"))
                    return@launch
                }
                !PASSWORD_REGEX.matches(password) -> {
                    _authState.postValue(AuthState.Error("Пароль должен содержать буквы и цифры"))
                    return@launch
                }
                password != confirmPassword -> {
                    _authState.postValue(AuthState.Error("Пароли не совпадают"))
                    return@launch
                }
            }

            // Хэширование пароля
            val (hash, salt) = PasswordHasher.hash(password)

            // Сохранение пользователя
            val user = User(
                email = email,
                passwordHash = hash,
                salt = salt
            )

            // Проверка существования пользователя и сохранение
            if (userRepository.isEmailExists(email)) {
                _authState.postValue(AuthState.Error("Email уже зарегистрирован"))
                return@launch
            }

            userRepository.insert(user)
            _authState.postValue(AuthState.Success("Регистрация успешна!"))
        }
    }

    fun loginUser(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = userRepository.getUserByEmail(email)

                if (user == null || !PasswordHasher.verify(password, user.passwordHash, user.salt)) {
                    _authState.postValue(AuthState.Error("Неверные учетные данные"))
                    return@launch
                }

                if (rememberMe) {
                    sessionManager.saveSession(email)
                } else {
                    sessionManager.clearSession()
                }

                _authState.postValue(AuthState.Success("Вход выполнен"))
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error(e.message ?: "Ошибка авторизации"))
            }
        }
    }
    fun checkSavedSession() {
        viewModelScope.launch {
            sessionManager.getSavedEmail()?.let { email ->
                _authState.postValue(AuthState.Success("Автовход для $email"))
            }
        }
    }
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}