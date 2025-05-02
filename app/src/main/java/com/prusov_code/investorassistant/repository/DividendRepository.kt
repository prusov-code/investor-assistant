package com.prusov_code.investorassistant.repository

import com.prusov_code.investorassistant.dao.DividendDao
import com.prusov_code.investorassistant.model.Dividend
import kotlinx.coroutines.flow.Flow

class DividendRepository(private val dividendDao: DividendDao) {
    val allDividends: Flow<List<Dividend>> = dividendDao.getAllDividends()
}