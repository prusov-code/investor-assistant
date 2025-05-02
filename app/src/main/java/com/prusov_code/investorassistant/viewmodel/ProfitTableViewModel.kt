package com.prusov_code.investorassistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prusov_code.investorassistant.model.DepositRecord
import com.prusov_code.investorassistant.repository.DepositRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProfitTableViewModel(
    private val repository: DepositRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ProfitTableState>(ProfitTableState.Loading)
    val state: StateFlow<ProfitTableState> = _state
    init {
        loadData()
    }
    private var _selectedStartDate: Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        timeInMillis
    }.timeInMillis

    private var _selectedEndDate: Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        timeInMillis
    }.timeInMillis

    val selectedStartDate: Long get() = _selectedStartDate
    val selectedEndDate: Long get() = _selectedEndDate

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getAllRecords()
                .distinctUntilChanged() // Добавляем фильтр уникальных значений
                .collect { records ->
                    if (records.isEmpty()) {
                        _state.value = ProfitTableState.Empty
                    } else {
                        calculateProfits(records.sortedBy { it.date })
                    }
                }
        }
    }
    private fun calculateProfits(records: List<DepositRecord>) {
        // Для расчетов используем сортировку по возрастанию
        val sortedAscending = records.sortedBy { it.date }

        // Для отображения сортируем по убыванию
        val sortedDescending = records.sortedByDescending { it.date }

        val totalProfit = calculateProfit(sortedAscending.first(), sortedAscending.last())
        val weeklyProfit = calculateProfitForPeriod(sortedAscending, 7)
        val monthlyProfit = calculateProfitForPeriod(sortedAscending, 30)
        val currentMonthProfit = calculateMonthlyProfit(sortedAscending)
        val customProfit = calculateCustomProfit(sortedAscending)

        _state.value = ProfitTableState.Data(
            records = sortedDescending, // Отображаем в обратном порядке
            totalProfit = formatPercentage(totalProfit),
            weeklyProfit = formatPercentage(weeklyProfit),
            monthlyProfit = formatPercentage(monthlyProfit),
            currentMonthProfit = formatPercentage(currentMonthProfit),
            customProfit = customProfit
        )
    }


    fun addDeposit(amount: Double, date: Long) {
        viewModelScope.launch {
            repository.insert(DepositRecord(amount = amount, date = date))
        }
    }

    fun deleteRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteRecord(id)
            // Явный запрос актуальных данных
            repository.getAllRecords().collect { records ->
                calculateProfits(records)
            }
        }
    }

    // Расчетные методы
    private fun calculateProfit(start: DepositRecord, end: DepositRecord): Double {
        require(start.date <= end.date) {
            "Начальная дата ${formatDate(start.date)} должна быть раньше конечной ${formatDate(end.date)}"
        }
        return ((end.amount - start.amount) / start.amount) * 100
    }
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(timestamp))
    }
    private fun calculateProfitForPeriod(records: List<DepositRecord>, days: Int): Double {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val periodRecords = records
            .filter { it.date >= calendar.timeInMillis }
            .sortedBy { it.date }

        return if (periodRecords.size >= 2) {
            calculateProfit(periodRecords.first(), periodRecords.last())
        } else 0.0
    }

    private fun calculateMonthlyProfit(records: List<DepositRecord>): Double {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val filtered = records.filter { it.date >= calendar.timeInMillis }
        return if (filtered.size >= 2) {
            calculateProfit(filtered.first(), filtered.last())
        } else 0.0
    }

    private fun calculateCustomProfit(records: List<DepositRecord>): String {
        val periodRecords = records
            .filter { it.date in selectedStartDate..selectedEndDate }
            .sortedBy { it.date } // Сортируем по возрастанию даты

        return when {
            periodRecords.size < 2 -> "Недостаточно данных"
            else -> {
                val first = periodRecords.first() // Самая ранняя запись
                val last = periodRecords.last()   // Самая поздняя запись
                val profit = calculateProfit(first, last)
                "Доходность за период: ${formatPercentage(profit)}"
            }
        }
    }
    fun updateDates(startDate: Long, endDate: Long) {
        _selectedStartDate = startDate
        _selectedEndDate = endDate
        loadData()
    }
    private fun formatPercentage(value: Double) = "%.2f%%".format(value)
}