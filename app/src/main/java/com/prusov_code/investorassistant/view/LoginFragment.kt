package com.prusov_code.investorassistant.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.prusov_code.investorassistant.R
import com.prusov_code.investorassistant.databinding.FragmentLoginBinding
import com.prusov_code.investorassistant.viewmodel.AuthState
import com.prusov_code.investorassistant.viewmodel.AuthViewModel
import com.prusov_code.investorassistant.viewmodel.AuthViewModelFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Проверка сохраненной сессии при запуске
        viewModel.checkSavedSession()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rememberMe = binding.cbRememberMe.isChecked

            if (validateInput(email, password)) {
                viewModel.loginUser(email, password, rememberMe)
            }
        }
        binding.tvRegisterLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    showLoading(true)
                }
                is AuthState.Success -> {
                    showLoading(false)
                    if (state.message.contains("Автовход")) {
                        navigateToMain(state.message)
                    } else {
                        handleSuccess(state)
                    }
                }
                is AuthState.Error -> {
                    showLoading(false)
                    showError(state.message)
                    viewModel.resetState()
                }
                AuthState.Idle -> {
                    showLoading(false)
                }
            }
        }
    }
    private fun navigateToMain(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_login_to_main)
    }


    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                showError("Введите email")
                false
            }
            password.isEmpty() -> {
                showError("Введите пароль")
                false
            }
            else -> true
        }
    }

    private fun handleSuccess(state: AuthState.Success) {
        showLoading(false)
        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_login_to_main)
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