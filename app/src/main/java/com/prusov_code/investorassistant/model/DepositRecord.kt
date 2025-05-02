package com.prusov_code.investorassistant.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "deposit_records",
    indices = [Index(value = ["date"], unique = true)] // Уникальность по дате
)
data class DepositRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val date: Long
)