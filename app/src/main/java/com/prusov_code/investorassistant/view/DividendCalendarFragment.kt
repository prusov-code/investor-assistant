package com.prusov_code.investorassistant.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.prusov_code.investorassistant.adapter.DividendAdapter
import com.prusov_code.investorassistant.database.DatabaseProvider
import com.prusov_code.investorassistant.databinding.FragmentDividendCalendarBinding
import com.prusov_code.investorassistant.repository.DividendRepository
import com.prusov_code.investorassistant.viewmodel.DividendState
import com.prusov_code.investorassistant.viewmodel.DividendViewModel
import com.prusov_code.investorassistant.viewmodel.DividendViewModelFactory
import kotlinx.coroutines.launch

class DividendCalendarFragment : Fragment() {
    private var _binding: FragmentDividendCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DividendAdapter

    private val viewModel: DividendViewModel by viewModels {
        DividendViewModelFactory(
            DividendRepository(
                DatabaseProvider.getDatabase(requireContext()).dividendDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDividendCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = DividendAdapter()
        binding.rvDividends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DividendCalendarFragment.adapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is DividendState.Loading -> showLoading(true)
                    is DividendState.Success -> handleSuccess(state)
                    is DividendState.Error -> handleError(state)
                    DividendState.Empty -> handleEmptyState()
                }
            }
        }
    }

    private fun handleSuccess(state: DividendState.Success) {
        showLoading(false)
        adapter.submitList(state.dividends)
        binding.tvEmpty.visibility = View.GONE
    }

    private fun handleError(state: DividendState.Error) {
        showLoading(false)
        binding.tvEmpty.visibility = View.VISIBLE
        binding.tvEmpty.text = state.message
    }

    private fun handleEmptyState() {
        showLoading(false)
        binding.tvEmpty.visibility = View.VISIBLE
        binding.tvEmpty.text = "Нет данных о дивидендах"
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}