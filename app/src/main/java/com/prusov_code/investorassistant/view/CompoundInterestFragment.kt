package com.prusov_code.investorassistant.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.prusov_code.investorassistant.databinding.FragmentCompoundInterestBinding
import com.prusov_code.investorassistant.viewmodel.CalculatorState
import com.prusov_code.investorassistant.viewmodel.CompoundInterestViewModel
import kotlinx.coroutines.launch

class CompoundInterestFragment : Fragment() {
    private var _binding: FragmentCompoundInterestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CompoundInterestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompoundInterestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTextListeners()
        setupObservers()
        setupButton()
    }

    private fun setupTextListeners() {
        binding.etPrincipal.addTextChangedListener(createTextWatcher { viewModel.updatePrincipal(it) })
        binding.etRate.addTextChangedListener(createTextWatcher { viewModel.updateRate(it) })
        binding.etYears.addTextChangedListener(createTextWatcher { viewModel.updateYears(it) })
    }

    private fun createTextWatcher(onTextChanged: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                onTextChanged(s?.toString() ?: "")
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is CalculatorState.Input -> handleInputState(state)
                    is CalculatorState.Result -> handleResult(state)
                    is CalculatorState.Error -> handleError(state)
                    CalculatorState.Loading -> showLoading(true)
                }
            }
        }
    }

    private fun handleInputState(state: CalculatorState.Input) {
        showLoading(false)
        with(binding) {
            if (etPrincipal.text.toString() != state.principal) etPrincipal.setText(state.principal)
            if (etRate.text.toString() != state.rate) etRate.setText(state.rate)
            if (etYears.text.toString() != state.years) etYears.setText(state.years)
            tvResult.text = ""
        }
    }

    private fun handleResult(state: CalculatorState.Result) {
        showLoading(false)
        binding.tvResult.text = state.amount
    }

    private fun handleError(state: CalculatorState.Error) {
        showLoading(false)
        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupButton() {
        binding.btnCalculate.setOnClickListener {
            viewModel.calculate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}