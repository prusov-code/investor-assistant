package com.prusov_code.investorassistant.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prusov_code.investorassistant.dao.DepositDao
import com.prusov_code.investorassistant.dao.DividendDao
import com.prusov_code.investorassistant.dao.UserDao
import com.prusov_code.investorassistant.model.DepositRecord
import com.prusov_code.investorassistant.model.Dividend
import com.prusov_code.investorassistant.model.User

@Database(
    entities = [User::class, DepositRecord::class, Dividend::class],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun depositDao(): DepositDao
    abstract fun dividendDao(): DividendDao
    abstract fun userDao(): UserDao
    companion object {
        const val DATABASE_NAME = "investor_db"
    }
}