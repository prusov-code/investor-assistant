package com.prusov_code.investorassistant.dao

import androidx.room.*
import com.prusov_code.investorassistant.model.DepositRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposit_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<DepositRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DepositRecord)

    @Query("DELETE FROM deposit_records WHERE id = :id")
    suspend fun deleteRecord(id: Int)
}