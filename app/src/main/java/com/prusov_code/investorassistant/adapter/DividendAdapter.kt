package com.prusov_code.investorassistant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prusov_code.investorassistant.R
import com.prusov_code.investorassistant.databinding.ItemDividendBinding
import com.prusov_code.investorassistant.model.Dividend

class DividendAdapter : ListAdapter<Dividend, DividendAdapter.ViewHolder>(DividendDiffCallback()) {

    inner class ViewHolder(private val binding: ItemDividendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dividend: Dividend) {
            with(binding) {
                // Загрузка логотипа (используем Glide)
                Glide.with(itemView.context)
                    .load(dividend.logoUrl ?: R.drawable.ic_calculator)
                    .into(ivLogo)

                tvCompanyName.text = "${dividend.ticker} - ${dividend.companyName}"
                tvDividendAmount.text = "Дивиденд: ${"%.2f".format(dividend.amount)} ₽"
                tvLastBuyDate.text = "Последняя покупка:\n${dividend.lastBuyDate}"
                tvRegistryDate.text = "Закрытие реестра:\n${dividend.registryCloseDate}"
                tvDividendYield.text = "Доходность: ${"%.1f".format(dividend.dividendYield)}%"
            }
        }
    }

    class DividendDiffCallback : DiffUtil.ItemCallback<Dividend>() {
        override fun areItemsTheSame(oldItem: Dividend, newItem: Dividend) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Dividend, newItem: Dividend) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDividendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}