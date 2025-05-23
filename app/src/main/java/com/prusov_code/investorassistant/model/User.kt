package com.prusov_code.investorassistant.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val passwordHash: String, // Хэш пароля
    val salt: String,         // Соль для хэширования
    val registrationDate: Long = System.currentTimeMillis()
)