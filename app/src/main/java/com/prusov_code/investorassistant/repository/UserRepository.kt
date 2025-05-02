package com.prusov_code.investorassistant.repository

import com.prusov_code.investorassistant.database.DatabaseProvider
import com.prusov_code.investorassistant.model.User
import android.content.Context

class UserRepository(context: Context) {
    private val userDao = DatabaseProvider.getDatabase(context).userDao()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun isEmailExists(email: String): Boolean {
        return userDao.isEmailExists(email)
    }
}