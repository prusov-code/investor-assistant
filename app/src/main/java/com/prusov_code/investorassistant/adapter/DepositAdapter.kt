package com.prusov_code.investorassistant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prusov_code.investorassistant.R
import com.prusov_code.investorassistant.databinding.ItemDepositBinding
import com.prusov_code.investorassistant.model.DepositRecord
import java.text.SimpleDateFormat
import java.util.*

class DepositAdapter(
    private val onDelete: (Int) -> Unit
) : ListAdapter<DepositRecord, DepositAdapter.ViewHolder>(DepositDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDepositBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position)
        val previousRecord = currentList
            .filter { it.date < record.date }
            .maxByOrNull { it.date }

        holder.bind(record, previousRecord)
    }

    inner class ViewHolder(private val binding: ItemDepositBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: DepositRecord, previousRecord: DepositRecord?) { // 2 параметра
            with(binding) {
                tvAmount.text = "Размер депозита: ${record.amount} ₽"
                tvDate.text = "Дата: ${formatDate(record.date)}"

                val percentage = calculatePercentage(
                    current = record.amount,
                    previous = previousRecord?.amount
                )

                tvPercentage.text = when {
                    previousRecord == null -> "Доходности нет"
                    percentage >= 0 -> "+${"%.2f".format(percentage)}%"
                    else -> "${"%.2f".format(percentage)}%"
                }

                tvPercentage.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        if (percentage >= 0) R.color.green else R.color.red
                    )
                )

                btnDelete.setOnClickListener { onDelete(record.id) }
            }
        }

        private fun calculatePercentage(current: Double, previous: Double?): Double {
            return if (previous == null || previous == 0.0) 0.0
            else ((current - previous) / previous) * 100
        }

        private fun formatDate(timestamp: Long): String {
            return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }

    class DepositDiffCallback : DiffUtil.ItemCallback<DepositRecord>() {
        override fun areItemsTheSame(oldItem: DepositRecord, newItem: DepositRecord) =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: DepositRecord, newItem: DepositRecord) =
            oldItem == newItem &&
                    oldItem.amount == newItem.amount &&
                    oldItem.date == newItem.date
    }
    override fun onCurrentListChanged(
        previousList: MutableList<DepositRecord>,
        currentList: MutableList<DepositRecord>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        notifyDataSetChanged() // Принудительное обновление всех элементов
    }
}