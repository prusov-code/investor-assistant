package com.prusov_code.investorassistant.repository

import android.content.Context
import com.prusov_code.investorassistant.database.DatabaseProvider
import com.prusov_code.investorassistant.model.DepositRecord
import kotlinx.coroutines.flow.Flow

class DepositRepository(context: Context) {
    private val dao = DatabaseProvider.getDatabase(context).depositDao()

    fun getAllRecords(): Flow<List<DepositRecord>> = dao.getAllRecords()

    suspend fun insert(record: DepositRecord) = dao.insert(record)
    suspend fun deleteRecord(id: Int) = dao.deleteRecord(id)
}