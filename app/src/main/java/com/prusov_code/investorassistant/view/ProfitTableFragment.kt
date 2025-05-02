// app/src/main/java/com/prusov_code/investorassistant/view/ProfitTableFragment.kt
package com.prusov_code.investorassistant.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.prusov_code.investorassistant.adapter.DepositAdapter
import com.prusov_code.investorassistant.databinding.FragmentProfitTableBinding
import com.prusov_code.investorassistant.viewmodel.ProfitTableState
import com.prusov_code.investorassistant.viewmodel.ProfitTableViewModel
import com.prusov_code.investorassistant.viewmodel.ProfitTableViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProfitTableFragment : Fragment() {
    private var _binding: FragmentProfitTableBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DepositAdapter
    private val viewModel: ProfitTableViewModel by viewModels {
        ProfitTableViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfitTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = DepositAdapter { viewModel.deleteRecord(it) }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProfitTableFragment.adapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ProfitTableState.Data -> handleDataState(state)
                    ProfitTableState.Loading -> showLoading(true)
                    is ProfitTableState.Error -> handleErrorState(state)
                    ProfitTableState.Empty -> handleEmptyState()
                }
            }
        }
    }

    private fun handleDataState(state: ProfitTableState.Data) {
        showLoading(false)
        adapter.submitList(state.records)
        with(binding) {
            tvTotalProfit.text = "Общая доходность: ${state.totalProfit}"
            tvWeeklyProfit.text = "За 7 дней: ${state.weeklyProfit}"
            tvMonthlyProfit.text = "За 30 дней: ${state.monthlyProfit}"
            tvCurrentMonthProfit.text = "Текущий месяц: ${state.currentMonthProfit}"
            tvCustomPeriodProfit.text = state.customProfit
        }
    }

    private fun handleErrorState(state: ProfitTableState.Error) {
        showLoading(false)
        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
    }

    private fun handleEmptyState() {
        showLoading(false)
        adapter.submitList(emptyList())
        with(binding) {
            tvTotalProfit.text ="Пока что нет данных. Добавьте записи с информацией о размере депозита при помощи кнопки \"Добавить запись\""
            tvWeeklyProfit.text = ""
            tvMonthlyProfit.text = ""
            tvCurrentMonthProfit.text = ""
            tvCustomPeriodProfit.text = ""
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupListeners() {
        binding.btnAddDeposit.setOnClickListener { showAddDepositDialog() }
        binding.btnStartDate.setOnClickListener { showDatePicker(true) }
        binding.btnEndDate.setOnClickListener { showDatePicker(false) }
    }

    private fun showAddDepositDialog() {
        AddDepositDialog { amount, date ->
            viewModel.addDeposit(amount, date)
        }.show(parentFragmentManager, "AddDepositDialog")
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = if (isStartDate) viewModel.selectedStartDate else viewModel.selectedEndDate
        }

        DatePickerDialog(requireContext(), { _, y, m, d ->
            val newCalendar = Calendar.getInstance().apply {
                set(y, m, d)
                if (isStartDate) {
                    set(Calendar.HOUR_OF_DAY, 0)
                } else {
                    set(Calendar.HOUR_OF_DAY, 23)
                }
            }

            val newDate = newCalendar.timeInMillis

            if (isStartDate) {
                viewModel.updateDates(
                    startDate = newDate,
                    endDate = viewModel.selectedEndDate
                )
                binding.btnStartDate.text = formatDate(newDate)
            } else {
                viewModel.updateDates(
                    startDate = viewModel.selectedStartDate,
                    endDate = newDate
                )
                binding.btnEndDate.text = formatDate(newDate)
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(timestamp))
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}