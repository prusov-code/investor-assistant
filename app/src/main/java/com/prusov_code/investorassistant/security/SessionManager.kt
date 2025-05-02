package com.prusov_code.investorassistant.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.*

class SessionManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_EMAIL = "email"
        private const val KEY_EXPIRY = "expiry_date"
    }

    fun saveSession(email: String, days: Int = 30) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, days)
        }
        with(sharedPreferences.edit()) {
            putString(KEY_EMAIL, email)
            putLong(KEY_EXPIRY, calendar.timeInMillis)
            apply()
        }
    }

    fun getSavedEmail(): String? {
        val expiry = sharedPreferences.getLong(KEY_EXPIRY, 0)
        return if (expiry > Calendar.getInstance().timeInMillis) {
            sharedPreferences.getString(KEY_EMAIL, null)
        } else {
            clearSession()
            null
        }
    }

    fun clearSession() {
        with(sharedPreferences.edit()) {
            remove(KEY_EMAIL)
            remove(KEY_EXPIRY)
            apply()
        }
    }
}