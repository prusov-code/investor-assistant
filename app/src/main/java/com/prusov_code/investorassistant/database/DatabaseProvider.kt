package com.prusov_code.investorassistant.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.prusov_code.investorassistant.model.Dividend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseProvider {
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        initDefaultDividends(context)
                    }
                }
            }).fallbackToDestructiveMigration()
                .build()
            database = instance
            instance
        }
    }
    private suspend fun initDefaultDividends(context: Context) {
        val dividendDao = database?.dividendDao() ?: return
        // Проверяем, не добавлены ли уже данные
        if (dividendDao.getCount() == 0) {
            val defaultDividends = listOf(
                Dividend(
                    ticker = "SBER",
                    companyName = "Сбербанк во 2024",
                    amount = 34.84,
                    lastBuyDate = "17.07.2025",
                    registryCloseDate = "18.07.2025",
                    dividendYield = 11.09,
                    logoUrl = "https://mybroker.storage.bcs.ru/FinInstrumentLogo/RU0009029540.png"
                ),
                Dividend(
                    ticker = "VTBR",
                    companyName = "BT5 во 2024",
                    amount = 25.58,
                    lastBuyDate = "10.07.2025",
                    registryCloseDate = "11.07.2025",
                    dividendYield = 25.96,
                    logoUrl = "https://mybroker.storage.bcs.ru/FinInstrumentLogo/RU000A0JP5V6.png"
                ),
                Dividend(
                    ticker = "ROSN",
                    companyName = "Роснефть во 2024",
                    amount = 14.68,
                    lastBuyDate = "17.07.2025",
                    registryCloseDate = "20.07.2025",
                    dividendYield = 3.1,
                    logoUrl = "https://mybroker.storage.bcs.ru/FinInstrumentLogo/RU000A0J2Q06.png"
                ),
                Dividend(
                    ticker = "X5",
                    companyName = "X5 во 2024",
                    amount = 648.0,
                    lastBuyDate = "08.07.2025",
                    registryCloseDate = "09.07.2025",
                    dividendYield = 19.02,
                    logoUrl = "https://mybroker.storage.bcs.ru/FinInstrumentLogo/RU000A108X38.png"
                ),
                Dividend(
                    ticker = "MOEX",
                    companyName = "Московская биржа во 2024",
                    amount = 26.11,
                    lastBuyDate = "09.07.2025",
                    registryCloseDate = "10.07.2025",
                    dividendYield = 12.82,
                    logoUrl = "https://mybroker.storage.bcs.ru/FinInstrumentLogo/RU000A0JR4A1.png"
                ),
                Dividend(
                    ticker = "SFIN",
                    companyName = "Эсэфай во 2024",
                    amount = 83.5,
                    lastBuyDate = "06.06.2025",
                    registryCloseDate = "09.06.2025",
                    dividendYield = 5.72,
                    logoUrl = "https://mybroker.storage.bcs.ru/FinInstrumentLogo/RU000A0JVW89.png"
                ),
                Dividend(
                    ticker = "BELU",
                    companyName = "Novabev Group 2024",
                    amount = 25.0,
                    lastBuyDate = "06.06.2025",
                    registryCloseDate = "09.06.2025",
                    dividendYield = 5.01,
                    logoUrl = "https://mybroker.storage.bcs.ru/FinInstrumentLogo/RU000A0HL5M1.png"
                ),
                Dividend(
                    ticker = "SVAV",
                    companyName = "COЛЛЕРС во 2024",
                    amount = 70.0,
                    lastBuyDate = "10.06.2025",
                    registryCloseDate = "11.06.2025",
                    dividendYield = 9.87,
                    logoUrl = "https://mybroker.storage.bcs.ru/FinInstrumentLogo/RU0006914488.png"
                ),
                Dividend(
                    ticker = "GEMA",
                    companyName = "MMLIS во 2024",
                    amount = 2.4,
                    lastBuyDate = "13.06.2025",
                    registryCloseDate = "16.06.2025",
                    dividendYield = 1.89,
                    logoUrl = "https://mybroker.storage.bcs.ru/FinInstrumentLogo/RU000A100GC7.png"
                )
            )
            defaultDividends.forEach {
                dividendDao.insert(it)
                Log.d("DEBUG", "Добавлен дивиденд: ${it.ticker}")
            }
        }
    }
}