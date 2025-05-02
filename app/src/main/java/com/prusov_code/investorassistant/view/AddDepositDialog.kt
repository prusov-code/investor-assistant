package com.prusov_code.investorassistant.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.prusov_code.investorassistant.databinding.DialogAddDepositBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddDepositDialog(
    private val onAdd: (Double, Long) -> Unit
) : DialogFragment() {
    private var _binding: DialogAddDepositBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddDepositBinding.inflate(inflater, container, false)
        // Устанавливаем текущую дату по умолчанию
        setCurrentDate()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Обработчик клика по полю даты
        binding.etDate.setOnClickListener {
            showDatePickerDialog()
        }
        // Обработчик нажатия на кнопку "Сохранить"
        binding.btnSave.setOnClickListener {
            onSaveClicked()
        }
    }
    private fun setCurrentDate() {
        val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        binding.etDate.setText(currentDate)
    }
    // Функция сохранения данных
    private fun onSaveClicked() {
        val amountStr = binding.etAmount.text.toString()
        val dateStr = binding.etDate.text.toString()

        if (amountStr.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: run {
            Toast.makeText(requireContext(), "Некорректная сумма", Toast.LENGTH_SHORT).show()
            return
        }

        val date = parseDate(dateStr) ?: run {
            Toast.makeText(requireContext(), "Некорректная дата (дд.мм.гггг)", Toast.LENGTH_SHORT).show()
            return
        }

        onAdd(amount, date) // Передаем данные наружу
        dismiss()
    }
    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)

            // Форматируем выбранную дату
            val formattedDate = String.format(
                "%02d.%02d.%04d",
                selectedDay,
                selectedMonth + 1, // Месяцы начинаются с 0
                selectedYear
            )

            binding.etDate.setText(formattedDate)
        }, year, month, day).show()
    }
    private fun parseDate(dateStr: String): Long? {
        return try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(dateStr)
            date?.time
        } catch (e: Exception) {
            null
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}