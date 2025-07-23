package com.crypto.prices.view.ui.more

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.asf.cryptoprices.databinding.ItemCurrencySelectBinding
import com.crypto.prices.utils.CurrencyData
import com.crypto.prices.utils.Utility

class CurrencySelectAdapter(
    private val context: Context?,
    private var data: List<CurrencyData>?,
    private val itemClick: ItemClick
) : RecyclerView.Adapter<CurrencySelectAdapter.CryptoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val binding = ItemCurrencySelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CryptoViewHolder(binding, itemClick)
    }

    override fun getItemCount() = data?.size ?: 0

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        context?.let { ctx ->
            data?.get(position)?.let { currencyData ->
                holder.bind(ctx, currencyData)
            }
        }
    }

    class CryptoViewHolder(
        private val binding: ItemCurrencySelectBinding,
        private val itemClick: ItemClick
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, data: CurrencyData) {
            binding.textViewName.text = "${data.name} (${data.symbol})"

            val currentSelectedCurrency = Utility.getCurrency(context as Activity)
            if (data.currency == currentSelectedCurrency) {
                binding.parentLayout.background = context.getDrawable(R.drawable.list_pressed)
                val colorRes = R.color.selected_text_color
                val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.resources.getColor(colorRes, context.theme)
                } else {
                    context.resources.getColor(colorRes)
                }
                binding.textViewName.setTextColor(color)
            } else {
                binding.parentLayout.background = context.getDrawable(R.drawable.list_not_pressed)
                val colorRes = R.color.black
                val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.resources.getColor(colorRes, context.theme)
                } else {
                    context.resources.getColor(colorRes)
                }
                binding.textViewName.setTextColor(color)
            }

            binding.parentLayout.setOnClickListener {
                Utility.setCurrency(context as Activity, data.currency)
                Utility.setCurrencyName(context, data.name)
                Utility.setCurrencySymbol(context, data.symbol)
                itemClick.onItemClicked()
                Utility.restartApp(context)
            }
        }
    }

    interface ItemClick {
        fun onItemClicked()
    }
}