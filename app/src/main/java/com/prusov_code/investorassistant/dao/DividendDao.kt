package com.prusov_code.investorassistant.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prusov_code.investorassistant.model.Dividend
import kotlinx.coroutines.flow.Flow

@Dao
interface DividendDao {
    @Query("SELECT * FROM dividends ORDER BY last_buy_date DESC")
    fun getAllDividends(): Flow<List<Dividend>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dividend: Dividend)
    @Query("SELECT COUNT(id) FROM dividends")
    suspend fun getCount(): Int
}
