package com.prusov_code.investorassistant.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dividends")
data class Dividend(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticker: String,
    val companyName: String,
    val amount: Double,
    @ColumnInfo(name = "last_buy_date") val lastBuyDate: String,
    @ColumnInfo(name = "registry_date") val registryCloseDate: String,
    val dividendYield: Double,
    val logoUrl: String? = null
)