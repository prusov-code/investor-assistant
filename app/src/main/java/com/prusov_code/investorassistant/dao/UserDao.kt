package com.prusov_code.investorassistant.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.prusov_code.investorassistant.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT EXISTS(SELECT * FROM users WHERE email = :email)")
    suspend fun isEmailExists(email: String): Boolean
}