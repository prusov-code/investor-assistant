package com.prusov_code.investorassistant.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.prusov_code.investorassistant.R
import com.prusov_code.investorassistant.databinding.FragmentRegisterBinding
import com.prusov_code.investorassistant.viewmodel.AuthState
import com.prusov_code.investorassistant.viewmodel.AuthViewModel
import com.prusov_code.investorassistant.viewmodel.AuthViewModelFactory

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is AuthState.Loading -> showLoading(true)
                is AuthState.Success -> handleSuccess(state)
                is AuthState.Error -> handleError(state)
                AuthState.Idle -> showLoading(false)
            }
        })
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInput(email, password, confirmPassword)) {
                val confirmPassword = binding.etConfirmPassword.text.toString().trim()
                viewModel.registerUser(email, password, confirmPassword)
            }
        }

        binding.tvLoginLink.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        return when {
            email.isEmpty() -> {
                showError("Введите email")
                false
            }
            password.isEmpty() -> {
                showError("Введите пароль")
                false
            }
            password != confirmPassword -> {
                showError("Пароли не совпадают")
                false
            }
            else -> true
        }
    }

    private fun handleSuccess(state: AuthState.Success) {
        showLoading(false)
        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_register_to_login)
        viewModel.resetState()
    }

    private fun handleError(state: AuthState.Error) {
        showLoading(false)
        showError(state.message)
        viewModel.resetState()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}